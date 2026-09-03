package com.marcuschu.webcodegenerate.common;

import com.marcuschu.webcodegenerate.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

/**
 * 统一封装接口返回格式
 * @param <T> T
 */
@Data
public class BaseResponse<T> implements Serializable {

    /**
     * 响应码
     */
    private int code;

    /**
     * 响应内容
     */
    private T data;


    /**
     * 错误说明
     */
    private String message;

    /**
     * 构造函数
     * @param code 响应码
     * @param data 响应内容
     * @param message 响应说明
     */
    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }


    /**
     * 构造函数
     * @param code 响应码
     * @param data 响应内容
     */
    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    /**
     * 构造函数
     * @param errorCode 错误响应枚举
     */
    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

}
