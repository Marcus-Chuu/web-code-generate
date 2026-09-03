package com.marcuschu.webcodegenerate.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserRoleEnum {

    /**
     * 普通用户
     */
    USER("用户", "user"),

    /**
     * 管理员
     */
    ADMIN("管理员", "admin");


    /**
     * 角色身份
     */
    private final String text;

    /**
     * 角色身份标识
     */
    private final String value;


    /**
     * 构造函数
     * @param text 身份名称
     * @param value 身份标识
     */
    UserRoleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     * @param value 枚举值的 value
     * @return 枚举值
     */
    public static UserRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (UserRoleEnum anEnum : UserRoleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
