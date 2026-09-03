package com.marcuschu.webcodegenerate.ai.core;

import cn.hutool.json.JSONUtil;
import com.marcuschu.webcodegenerate.ai.AiCodeGeneratorFluxService;
import com.marcuschu.webcodegenerate.ai.AiCodeGeneratorServiceFluxFactory;
import com.marcuschu.webcodegenerate.ai.core.builder.VueProjectBuilder;
import com.marcuschu.webcodegenerate.ai.core.parser.CodeParserExecutor;
import com.marcuschu.webcodegenerate.ai.core.saver.CodeFileSaverExecutor;
import com.marcuschu.webcodegenerate.ai.message.AiResponseMessage;
import com.marcuschu.webcodegenerate.ai.message.ToolExecutedMessage;
import com.marcuschu.webcodegenerate.ai.message.ToolRequestMessage;
import com.marcuschu.webcodegenerate.constant.AppConstant;
import com.marcuschu.webcodegenerate.exception.BusinessException;
import com.marcuschu.webcodegenerate.exception.ErrorCode;
import com.marcuschu.webcodegenerate.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * AI 代码生成外观类, 组合生成和保存功能 (门面模式)
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {


    /**
     * AI 实例工厂类
     */
    @Resource
    private AiCodeGeneratorServiceFluxFactory aiCodeGeneratorServiceFluxFactory;



    /**
     * VUE 项目打包
     */
    @Resource
    private VueProjectBuilder vueProjectBuilder;




    /**
     * 统一入口 : 根据类型生成并保存代码（流式）
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成类型
     * @param appId 应用 ID
     */
    public Flux<String> generateAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        // 根据 appId 获取对应的 AI 服务实例
        AiCodeGeneratorFluxService aiCodeGeneratorFluxService = aiCodeGeneratorServiceFluxFactory.getAiCodeGeneratorFluxService(appId, codeGenTypeEnum);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case HTML -> {
                Flux<String> codeStream = aiCodeGeneratorFluxService.generateHtmlCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                Flux<String> codeStream = aiCodeGeneratorFluxService.generateMultiFileCodeStream(userMessage);
                yield processCodeStream(codeStream, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            case VUE_PROJECT -> {
                // Vue 工程由工具调用直接写入文件, 不能再按照 MULTI_FILE 响应解析和保存
                yield processTokenStream(aiCodeGeneratorFluxService.generateVueProjectCodeStream(appId, userMessage), appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型: " + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }



    /**
     * 通用流式代码处理方法
     * @param codeStream  代码流
     * @param codeGenType 代码生成类型
     * @param appId 应用 ID
     * @return 流式响应
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        return codeStream.doOnNext(chunk -> {
            // 实时收集代码片段
            codeBuilder.append(chunk);
        }).doOnComplete(() -> {
            // 流式返回完成后保存代码
            try {
                String completeCode = codeBuilder.toString();
                // 使用执行器解析代码
                Object parsedResult = CodeParserExecutor.executeParser(completeCode, codeGenType);
                // 使用执行器保存代码
                File savedDir = CodeFileSaverExecutor.executeSaver(parsedResult, codeGenType, appId);
                log.info("保存成功, 路径为：" + savedDir.getAbsolutePath());
            } catch (Exception e) {
                log.error("保存失败 : {}", e.getMessage());
            }
        });
    }



    /**
     * 将 TokenStream 转换为 Flux<String> , 并传递工具调用信息
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream, Long appId) {
        return Flux.create(sink -> {
            tokenStream
                    .onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                    })
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                        sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        // 异步构造 Vue 项目
                        String projectPath = AppConstant.CODE_OUTPUT_ROOT_DIR + "/vue_project_" + appId;
                        vueProjectBuilder.buildProjectAsync(projectPath);
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }






}
