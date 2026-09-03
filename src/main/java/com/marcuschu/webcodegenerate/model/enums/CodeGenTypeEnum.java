package com.marcuschu.webcodegenerate.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum CodeGenTypeEnum {

    /**
     * HTML 模式
     */
    HTML("原生 HTML 模式", "html"),


    /**
     * 原生多文件模型
     */
    MULTI_FILE("原生多文件模式", "multi_file"),


    /**
     * VUE 工程模式
     */
    VUE_PROJECT("Vue 工程模式", "vue_project");


    /**
     * 模式名称
     */
    private final String text;


    /**
     * 模式标识
     */
    private final String value;


    /**
     * 代码模式构造函数
     * @param text 名称
     * @param value 标识
     */
    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }


    /**
     * 根据 value 获取枚举
     * @param value 枚举值的 value
     * @return 枚举值
     */
    public static CodeGenTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
