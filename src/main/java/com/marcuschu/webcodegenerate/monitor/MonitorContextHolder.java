package com.marcuschu.webcodegenerate.monitor;

import reactor.core.publisher.Flux;

/** 同线程内访问监控上下文 : 跨线程传递快照，在执行期间恢复。 */
public final class MonitorContextHolder {
    private static final ThreadLocal<MonitorContext> CONTEXT_HOLDER = new ThreadLocal<>();

    private MonitorContextHolder() {
    }

    public static void setContext(MonitorContext context) {
        if (context == null) {
            CONTEXT_HOLDER.remove();
        } else {
            CONTEXT_HOLDER.set(context);
        }
    }

    public static MonitorContext getContext() {
        return CONTEXT_HOLDER.get();
    }

    public static void clearContext() {
        CONTEXT_HOLDER.remove();
    }

    public static MonitorContext capture() {
        MonitorContext context = getContext();
        return context == null ? null : new MonitorContext(context.getUserId(), context.getAppId());
    }

    /** Scope 必须在创建它的线程中关闭；支持嵌套调用并恢复原值。 */
    public static Scope open(MonitorContext context) {
        Scope scope = new Scope(getContext());
        setContext(context);
        return scope;
    }

    /** Flux 的订阅可能晚于业务方法返回，在实际订阅期间恢复上下文。 */
    public static <T> Flux<T> bind(Flux<T> source, MonitorContext context) {
        MonitorContext snapshot = context == null ? null
                : new MonitorContext(context.getUserId(), context.getAppId());
        return Flux.from(subscriber -> {
            try (Scope ignored = open(snapshot)) {
                source.subscribe(subscriber);
            }
        });
    }

    public static final class Scope implements AutoCloseable {
        private final MonitorContext previous;
        private boolean closed;

        private Scope(MonitorContext previous) {
            this.previous = previous;
        }

        @Override
        public void close() {
            if (!closed) {
                setContext(previous);
                closed = true;
            }
        }
    }
}
