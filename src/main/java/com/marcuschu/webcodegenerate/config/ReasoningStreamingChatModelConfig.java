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
 * 推理模型配置
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.reasoning-streaming-chat-model")
@Data
public class ReasoningStreamingChatModelConfig {

    /**
     * 普罗米修斯模型使用情况监控
     */
    @Resource
    private AiModelMonitorListener aiModelMonitorListener;

    /**
     * 调用模型的 url
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
     * 最大消耗 Token
     */
    private Integer maxTokens;

    /**
     * 模型温度 : 值比较低时输出会比较稳定
     */
    private Double temperature;

    /**
     * 请求日志
     */
    private Boolean logRequests = false;

    /**
     * 输出日志
     */
    private Boolean logResponses = false;


    /**
     * 推理流式模型 (用于 Vue 项目生成, 带工具调用)
     */
    @Bean
    @Scope("prototype")
    public StreamingChatModel reasoningStreamingChatModelPrototype() {
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