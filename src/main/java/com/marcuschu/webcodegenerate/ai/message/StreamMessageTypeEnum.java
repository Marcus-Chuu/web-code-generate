package com.marcuschu.webcodegenerate.ai.message;

import lombok.Getter;

/**
 * 流式消息类型枚举
 */
@Getter
public enum StreamMessageTypeEnum {

    /**
     * AI 响应内容
     */
    AI_RESPONSE("ai_response", "AI响应"),


    /**
     * 工具请求内容
     */
    TOOL_REQUEST("tool_request", "工具请求"),


    /**
     * 工具执行结果
     */
    TOOL_EXECUTED("tool_executed", "工具执行结果");


    /**
     * 流式消息类型标识
     */
    private final String value;


    /**
     * 流式消息类型
     */
    private final String text;


    /**
     * 消息类型枚举构造函数
     * @param value 类型标识
     * @param text  类型
     */
    StreamMessageTypeEnum(String value, String text) {
        this.value = value;
        this.text = text;
    }


    /**
     * 根据值获取枚举
     */
    public static StreamMessageTypeEnum getEnumByValue(String value) {
        for (StreamMessageTypeEnum typeEnum : values()) {
            if (typeEnum.getValue().equals(value)) {
                return typeEnum;
            }
        }
        return null;
    }
}