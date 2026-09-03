package com.marcuschu.webcodegenerate.model.request.app;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建应用请求
 */
@Data
public class AppAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;
}

