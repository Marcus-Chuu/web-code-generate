package com.marcuschu.webcodegenerate.ai;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.marcuschu.webcodegenerate.ai.guardrail.PromptSafetyInputGuardrail;
import com.marcuschu.webcodegenerate.ai.tools.ToolManager;
import com.marcuschu.webcodegenerate.exception.BusinessException;
import com.marcuschu.webcodegenerate.exception.ErrorCode;
import com.marcuschu.webcodegenerate.model.enums.CodeGenTypeEnum;
import com.marcuschu.webcodegenerate.service.ChatHistoryService;
import com.marcuschu.webcodegenerate.utils.SpringContextUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Slf4j
@Configuration
public class AiCodeGeneratorServiceFluxFactory {


    /**
     * 模型注入
     */
    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;


    /**
     * 对话历史服务
     */
    @Resource
    private ChatHistoryService chatHistoryService;


    /**
     * 工具管理服务
     */
    @Resource
    private ToolManager toolManager;


    /**
     * Redis 记忆存储
     */
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;



    /**
     * AI 服务实例缓存
     * 缓存策略 :
     *  - 最大缓存 1000 个实例
     *  - 写入后 30 分钟过期
     *  - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorFluxService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除, appId : {}, 原因 : {}", key, cause);
            })
            .build();



    /**
     * 根据 appId 获取 AI 应用类
     * @return AiCodeGeneratorFluxService
     */
    @Bean
    public AiCodeGeneratorFluxService aiCodeGeneratorFluxService() {
        return getAiCodeGeneratorFluxService(0L);
    }



    /**
     * 根据 appId 获取服务 ( 带缓存 )
     * @param appId 应用 ID
     * @return AiCodeGeneratorService
     */
    public AiCodeGeneratorFluxService getAiCodeGeneratorFluxService(long appId) {
        return getAiCodeGeneratorFluxService(appId, CodeGenTypeEnum.HTML);
    }



    /**
     * 根据 appId 获取服务 ( 带缓存 )
     * @param appId 应用 ID
     * @return AiCodeGeneratorService
     */
    public AiCodeGeneratorFluxService getAiCodeGeneratorFluxService(long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey, key -> createAiCodeGeneratorFluxService(appId, codeGenType));
    }


    /**
     * 构建缓存键
     * @param appId appId
     * @param codeGenType 类型
     * @return String
     */
    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType) {
        return appId + "_" + codeGenType.getValue();
    }



    /**
     * 创建新的 AI 服务实例
     */
    private AiCodeGeneratorFluxService createAiCodeGeneratorFluxService(long appId, CodeGenTypeEnum codeGenType) {
        // 根据 appId 构建独立的对话记忆
        MessageWindowChatMemory chatMemory = MessageWindowChatMemory
                .builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        // 从数据库加载历史对话到记忆中
        chatHistoryService.loadChatHistoryToMemory(appId, chatMemory, 20);
        // 根据代码生成类型选择不同的模型配置
        return switch (codeGenType) {
            case VUE_PROJECT -> {
                // 使用多例模式的 StreamingChatModel 解决并发问题
                StreamingChatModel reasoningStreamingChatModel = SpringContextUtil.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorFluxService.class)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemoryProvider(memoryId -> chatMemory)
                        .tools(toolManager.getAllTools())
                        .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                                toolExecutionRequest, "Error: there is no tool called " + toolExecutionRequest.name()
                        ))
                        .inputGuardrails(new PromptSafetyInputGuardrail())
                        .maxSequentialToolsInvocations(20)
                        .build();
            }
            case HTML, MULTI_FILE -> {
                // 使用多例模式的 StreamingChatModel 解决并发问题
                StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorFluxService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .chatMemory(chatMemory)
                        .inputGuardrails(new PromptSafetyInputGuardrail())
                        .maxSequentialToolsInvocations(20)
                        .build();
            }
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                    "不支持的代码生成类型: " + codeGenType.getValue());
        };

    }




}
