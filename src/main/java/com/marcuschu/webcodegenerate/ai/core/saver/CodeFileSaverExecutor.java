package com.marcuschu.webcodegenerate.ai.core.saver;

import com.marcuschu.webcodegenerate.ai.model.HtmlCodeResult;
import com.marcuschu.webcodegenerate.ai.model.MultiFileCodeResult;
import com.marcuschu.webcodegenerate.exception.BusinessException;
import com.marcuschu.webcodegenerate.exception.ErrorCode;
import com.marcuschu.webcodegenerate.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 */
public class CodeFileSaverExecutor {


    /**
     * HTML 文件保存
     */
    private static final HtmlCodeFileSaverTemplate htmlCodeFileSaver = new HtmlCodeFileSaverTemplate();


    /**
     * 多文件保存
     */
    private static final MultiFileCodeFileSaverTemplate multiFileCodeFileSaver = new MultiFileCodeFileSaverTemplate();


    /**
     * 执行代码保存
     * @param codeResult  代码结果对象
     * @param codeGenType 代码生成类型
     * @param appId 应用 ID
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> multiFileCodeFileSaver.saveCode((MultiFileCodeResult) codeResult, appId);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeGenType);
        };
    }

}

