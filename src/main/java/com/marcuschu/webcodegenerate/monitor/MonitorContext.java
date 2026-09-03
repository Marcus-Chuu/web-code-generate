package com.marcuschu.webcodegenerate.monitor;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 监控上下文 (需要传递的数据)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonitorContext implements Serializable {


    /**
     * 用户工号
     */
    private String userId;


    /**
     * 应用 ID
     */
    private String appId;



    @Serial
    private static final long serialVersionUID = 1L;
}