package com.marcuschu.webcodegenerate.ai.core.parser;

import com.marcuschu.webcodegenerate.exception.BusinessException;
import com.marcuschu.webcodegenerate.exception.ErrorCode;
import com.marcuschu.webcodegenerate.model.enums.CodeGenTypeEnum;

/**
 * 代码解析执行器
 */
public class CodeParserExecutor {


    /**
     * HTML 解析器
     */
    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();


    /**
     * 多文件解析器
     */
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();


    /**
     * 生成的代码解析
     * @param codeContent 生成的代码内容
     * @param codeGenTypeEnum 解析内容
     * @return Object
     */
    public static Object executeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML -> htmlCodeParser.parseCode(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parseCode(codeContent);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenTypeEnum);
        };
    }


}
