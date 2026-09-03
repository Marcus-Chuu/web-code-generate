package com.marcuschu.webcodegenerate.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 对话消息类型枚举
 *
 * @author MarcusChu
 */
@Getter
public enum ChatHistoryMessageTypeEnum {

    /**
     * 用户消息
     */
    USER("用户消息", "user"),

    /**
     * AI 消息
     */
    AI("AI 消息", "ai");


    /**
     * 消息类型
     */
    private final String text;


    /**
     * 消息类型标识
     */
    private final String value;


    /**
     * 构造函数
     * @param text 消息类型
     * @param value 消息类型标识
     */
    ChatHistoryMessageTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static ChatHistoryMessageTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (ChatHistoryMessageTypeEnum messageTypeEnum : values()) {
            if (messageTypeEnum.value.equals(value)) {
                return messageTypeEnum;
            }
        }
        return null;
    }
}
