package com.marcuschu.webcodegenerate.monitor;

import com.marcuschu.webcodegenerate.langgraph4j.CodeGenWorkflow;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.listener.*;
import dev.langchain4j.model.chat.request.*;
import dev.langchain4j.model.chat.response.*;
import dev.langchain4j.model.output.TokenUsage;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.prebuilt.MessagesStateGraph;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

import static dev.langchain4j.model.ModelProvider.OTHER;
import static org.bsc.langgraph4j.StateGraph.START;
import static org.bsc.langgraph4j.StateGraph.END;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;
import static org.junit.jupiter.api.Assertions.*;

class MonitorContextPropagationTest {
    @AfterEach
    void cleanUp() {
        MonitorContextHolder.clearContext();
    }

    @Test
    void scopeRestoresPreviousContextEvenAfterFailure() {
        MonitorContext previous = new MonitorContext("previous", "app");
        MonitorContextHolder.setContext(previous);
        assertThrows(IllegalStateException.class, () -> {
            try (var ignored = MonitorContextHolder.open(new MonitorContext("current", "app"))) {
                assertEquals("current", MonitorContextHolder.getContext().getUserId());
                throw new IllegalStateException("expected");
            }
        });
        assertSame(previous, MonitorContextHolder.getContext());
    }

    @Test
    void deferredSubscriptionUsesSnapshotAndCleansReusedThread() throws Exception {
        MonitorContext context = new MonitorContext("user-a", "app-a");
        Flux<String> bound = MonitorContextHolder.bind(
                Flux.defer(() -> Flux.just(MonitorContextHolder.getContext().getUserId())), context);
        context.setUserId("changed-after-binding");
        try (ExecutorService executor = Executors.newSingleThreadExecutor()) {
            assertEquals("user-a", executor.submit(() -> bound.blockLast(Duration.ofSeconds(5))).get());
            assertNull(executor.submit(MonitorContextHolder::getContext).get());
            RuntimeException failure = new RuntimeException("subscription failed");
            Flux<String> failing = MonitorContextHolder.bind(Flux.error(failure), context);
            executor.submit(() -> assertSame(failure, assertThrows(RuntimeException.class,
                    () -> failing.blockLast(Duration.ofSeconds(5))))).get();
            assertNull(executor.submit(MonitorContextHolder::getContext).get());
        }
        assertNull(MonitorContextHolder.getContext());
    }

    @Test
    void absentContextDoesNotBreakAnyListenerCallback() {
        RecordingCollector collector = new RecordingCollector();
        AiModelMonitorListener listener = listener(collector);
        Map<Object, Object> attributes = new ConcurrentHashMap<>();
        ChatRequest request = request();
        assertDoesNotThrow(() -> listener.onRequest(new ChatModelRequestContext(request, OTHER, attributes)));
        assertDoesNotThrow(() -> listener.onResponse(new ChatModelResponseContext(response("ok"), request, OTHER, attributes)));
        assertDoesNotThrow(() -> listener.onError(new ChatModelErrorContext(new RuntimeException("failed"), request, OTHER, attributes)));
        assertTrue(collector.requests.isEmpty());
    }

    @Test
    void errorUsesRequestSnapshotInsteadOfCallbackThreadContext() {
        RecordingCollector collector = new RecordingCollector();
        AiModelMonitorListener listener = listener(collector);
        Map<Object, Object> attributes = new ConcurrentHashMap<>();
        MonitorContext original = new MonitorContext("request-user", "app");
        MonitorContextHolder.setContext(original);
        listener.onRequest(new ChatModelRequestContext(request(), OTHER, attributes));
        original.setUserId("mutated");
        MonitorContextHolder.setContext(new MonitorContext("unrelated-user", "other-app"));
        listener.onError(new ChatModelErrorContext(new RuntimeException("failed"), request(), OTHER, attributes));
        assertEquals(List.of("request-user:started", "request-user:error"), collector.requests);
        assertEquals(List.of("request-user"), collector.errors);
    }

    @Test
    void toolFollowUpKeepsContextAcrossAsyncRounds() throws Exception {
        RecordingCollector collector = new RecordingCollector();
        QueuedModel model = new QueuedModel(listener(collector));
        EchoTools tools = new EchoTools();
        ToolService service = AiServices.builder(ToolService.class).streamingChatModel(model).tools(tools).build();
        CompletableFuture<String> completed = new CompletableFuture<>();
        try (var ignored = MonitorContextHolder.open(new MonitorContext("tool-user", "app"))) {
            service.chat("Use echo and report the result")
                    .onPartialResponse(part -> {})
                    .onCompleteResponse(result -> completed.complete(result.aiMessage().text()))
                    .onError(completed::completeExceptionally)
                    .start();
        }
        assertNull(MonitorContextHolder.getContext());
        try (ExecutorService callback = Executors.newSingleThreadExecutor()) {
            StreamingChatResponseHandler first = model.next();
            callback.submit(() -> first.onCompleteResponse(response(AiMessage.from(
                    ToolExecutionRequest.builder().id("tool-1").name("echo").arguments("{}").build())))).get();
            assertEquals("tool-user", tools.observedUser);
            assertNull(callback.submit(MonitorContextHolder::getContext).get());
            StreamingChatResponseHandler second = model.next();
            callback.submit(() -> second.onCompleteResponse(response("finished"))).get();
            assertEquals("finished", completed.get(5, TimeUnit.SECONDS));
            assertNull(callback.submit(MonitorContextHolder::getContext).get());
        }
        assertEquals(List.of("tool-user:started", "tool-user:success", "tool-user:started", "tool-user:success"), collector.requests);
    }

    @Test
    void interleavedRequestsOnSharedModelStayIsolatedAndRestoreWorkerContext() throws Exception {
        QueuedModel model = new QueuedModel(listener(new RecordingCollector()));
        List<String> observed = new CopyOnWriteArrayList<>();
        try (var ignored = MonitorContextHolder.open(new MonitorContext("a", "app-a"))) {
            model.chat("a", observer(observed));
        }
        try (var ignored = MonitorContextHolder.open(new MonitorContext("b", "app-b"))) {
            model.chat("b", observer(observed));
        }
        StreamingChatResponseHandler first = model.next();
        StreamingChatResponseHandler second = model.next();
        try (ExecutorService worker = Executors.newSingleThreadExecutor()) {
            worker.submit(() -> {
                MonitorContext previous = new MonitorContext("worker", "worker-app");
                MonitorContextHolder.setContext(previous);
                try {
                    second.onPartialResponse("b");
                    first.onPartialResponse("a");
                    second.onError(new RuntimeException("b-error"));
                    first.onCompleteResponse(response("a"));
                    assertSame(previous, MonitorContextHolder.getContext());
                } finally {
                    MonitorContextHolder.clearContext();
                }
            }).get();
        }
        assertEquals(List.of("b", "a", "b", "a"), observed);
    }

    @Test
    void callbackFailureStillRestoresThreadContext() throws Exception {
        QueuedModel model = new QueuedModel(listener(new RecordingCollector()));
        try (var ignored = MonitorContextHolder.open(new MonitorContext("request", "app"))) {
            model.chat("hello", new StreamingChatResponseHandler() {
                public void onPartialResponse(String part) { throw new IllegalStateException("callback failure"); }
                public void onCompleteResponse(ChatResponse response) {}
                public void onError(Throwable error) {}
            });
        }
        StreamingChatResponseHandler callback = model.next();
        assertThrows(IllegalStateException.class, () -> callback.onPartialResponse("part"));
        assertNull(MonitorContextHolder.getContext());
    }

    @Test
    void workflowCapturesContextBeforeDelayedSubscriptionAndPassesItToVirtualThread() {
        AtomicReference<MonitorContext> observed = new AtomicReference<>();
        CodeGenWorkflow workflow = new CodeGenWorkflow() {
            @Override
            public CompiledGraph<MessagesState<String>> createWorkflow() {
                try {
                    return new MessagesStateGraph<String>()
                            .addNode("probe", node_async(state -> {
                                assertTrue(Thread.currentThread().isVirtual());
                                observed.set(MonitorContextHolder.capture());
                                return Map.of();
                            }))
                            .addEdge(START, "probe").addEdge("probe", END).compile();
                } catch (Exception error) {
                    throw new RuntimeException(error);
                }
            }
        };
        Flux<String> result;
        try (var ignored = MonitorContextHolder.open(new MonitorContext("workflow-user", "3"))) {
            result = workflow.executeWorkflowWithFlux("prompt", 3L);
        }
        assertNull(MonitorContextHolder.getContext());
        result.blockLast(Duration.ofSeconds(5));
        assertNotNull(observed.get());
        assertEquals("workflow-user", observed.get().getUserId());
        assertNull(MonitorContextHolder.getContext());
    }

    public interface ToolService {
        TokenStream chat(String message);
    }

    public static class EchoTools {
        volatile String observedUser;
        @Tool("Return a test result")
        public String echo() {
            observedUser = MonitorContextHolder.getContext().getUserId();
            return "echo-result";
        }
    }

    private static StreamingChatResponseHandler observer(List<String> observed) {
        return new StreamingChatResponseHandler() {
            public void onPartialResponse(String text) { observed.add(MonitorContextHolder.getContext().getUserId()); }
            public void onCompleteResponse(ChatResponse response) { observed.add(MonitorContextHolder.getContext().getUserId()); }
            public void onError(Throwable error) { observed.add(MonitorContextHolder.getContext().getUserId()); }
        };
    }

    private static ChatRequest request() {
        return ChatRequest.builder().messages(dev.langchain4j.data.message.UserMessage.from("test"))
                .parameters(DefaultChatRequestParameters.builder().modelName("test-model").build()).build();
    }

    private static ChatResponse response(String text) {
        return response(AiMessage.from(text));
    }

    private static ChatResponse response(AiMessage message) {
        return ChatResponse.builder().aiMessage(message)
                .metadata(ChatResponseMetadata.builder().modelName("test-model").tokenUsage(new TokenUsage(1, 1)).build())
                .build();
    }

    private static AiModelMonitorListener listener(RecordingCollector collector) {
        AiModelMonitorListener listener = new AiModelMonitorListener();
        ReflectionTestUtils.setField(listener, "aiModelMetricsCollector", collector);
        return listener;
    }

    private static class QueuedModel implements StreamingChatModel {
        final BlockingQueue<StreamingChatResponseHandler> callbacks = new LinkedBlockingQueue<>();
        final ChatModelListener listener;
        QueuedModel(ChatModelListener listener) { this.listener = listener; }
        public List<ChatModelListener> listeners() { return List.of(listener); }
        public ChatRequestParameters defaultRequestParameters() {
            return DefaultChatRequestParameters.builder().modelName("test-model").build();
        }
        public void doChat(ChatRequest request, StreamingChatResponseHandler handler) { callbacks.add(handler); }
        StreamingChatResponseHandler next() throws InterruptedException {
            StreamingChatResponseHandler handler = callbacks.poll(5, TimeUnit.SECONDS);
            assertNotNull(handler, "Expected a model request");
            return handler;
        }
    }

    private static class RecordingCollector extends AiModelMetricsCollector {
        final List<String> requests = new CopyOnWriteArrayList<>();
        final List<String> errors = new CopyOnWriteArrayList<>();
        public void recordRequest(String user, String app, String model, String status) { requests.add(user + ":" + status); }
        public void recordError(String user, String app, String model, String message) { errors.add(user); }
        public void recordResponseTime(String user, String app, String model, Duration duration) {}
        public void recordTokenUsage(String user, String app, String model, String type, long count) {}
    }
}
