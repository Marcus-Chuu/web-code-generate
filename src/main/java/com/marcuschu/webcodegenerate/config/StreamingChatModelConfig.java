package com.marcuschu.webcodegenerate.config;

import com.marcuschu.webcodegenerate.monitor.AiModelMonitorListener;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.List;

/**
 * 流式对话模型配置
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.streaming-chat-model")
@Data
public class StreamingChatModelConfig {

    /**
     * AI 监控
     */
    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    /**
     * 模型请求的 url
     */
    private String baseUrl;

    /**
     * api-key
     */
    private String apiKey;

    /**
     * 模型名称
     */
    private String modelName;

    /**
     * 最大 Token 消耗
     */
    private Integer maxTokens;

    /**
     * 模型温度 : 越低越稳定
     */
    private Double temperature;

    /**
     * 请求日志
     */
    private boolean logRequests;

    /**
     * 输出日志
     */
    private boolean logResponses;

    /**
     * 流式模型
     */
    @Bean
    @Scope("prototype")
    public StreamingChatModel streamingChatModelPrototype() {
        return OpenAiStreamingChatModel.builder()
                .apiKey(apiKey)
                .baseUrl(baseUrl)
                .modelName(modelName)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .listeners(List.of(aiModelMonitorListener))
                .build();
    }
}
