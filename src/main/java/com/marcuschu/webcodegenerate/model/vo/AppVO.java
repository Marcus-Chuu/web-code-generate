package com.marcuschu.webcodegenerate.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 应用脱敏视图
 */
@Data
public class AppVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用 ID
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化的提示词
     */
    private String initPrompt;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    /**
     * 应用部署的 key
     */
    private String deployKey;

    /**
     * 部署时间
     */
    private LocalDateTime deployedTime;

    /**
     * 应用优先级
     */
    private Integer priority;

    /**
     * 用户 ID
     */
    private Long userId;

    /**
     * 应用创建者脱敏信息
     */
    private UserVO user;

    /**
     * 应用编辑时间
     */
    private LocalDateTime editTime;

    /**
     * 应用创建时间
     */
    private LocalDateTime createTime;

    /**
     * 应用更新时间
     */
    private LocalDateTime updateTime;
}
