package com.marcuschu.webcodegenerate.ai.message;

import dev.langchain4j.service.tool.ToolExecution;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 工具执行结果消息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ToolExecutedMessage extends StreamMessage {

    /**
     * 工具 ID
     */
    private String id;

    /**
     * 工具名称
     */
    private String name;

    /**
     * 调用参数
     */
    private String arguments;

    /**
     * 工具调用结果
     */
    private String result;

    /**
     * 构造函数
     * @param toolExecution 工具执行
     */
    public ToolExecutedMessage(ToolExecution toolExecution) {
        super(StreamMessageTypeEnum.TOOL_EXECUTED.getValue());
        this.id = toolExecution.request().id();
        this.name = toolExecution.request().name();
        this.arguments = toolExecution.request().arguments();
        this.result = toolExecution.result();
    }
}