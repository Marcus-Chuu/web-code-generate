package com.marcuschu.webcodegenerate.config;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * 智能路由模型配置
 */
@Configuration
@ConfigurationProperties(prefix = "langchain4j.open-ai.routing-chat-model")
@Data
public class RoutingAiModelConfig {

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
    private Boolean logRequests = false;

    /**
     * 输出日志
     */
    private Boolean logResponses = false;


    /**
     * 创建用于路由判断的ChatModel
     */
    @Bean
    @Scope("prototype")
    public ChatModel routingChatModelPrototype() {
        return OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .maxTokens(maxTokens)
                .temperature(temperature)
                .logRequests(logRequests)
                .logResponses(logResponses)
                .build();
    }
}