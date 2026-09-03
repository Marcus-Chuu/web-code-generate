package com.marcuschu.webcodegenerate.langgraph4j.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.system.SystemUtil;
import com.marcuschu.webcodegenerate.exception.BusinessException;
import com.marcuschu.webcodegenerate.exception.ErrorCode;
import com.marcuschu.webcodegenerate.langgraph4j.state.ImageCategoryEnum;
import com.marcuschu.webcodegenerate.langgraph4j.state.ImageResource;
import com.marcuschu.webcodegenerate.utils.CosManager;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MermaidDiagramTool {

    private static final int MAX_COMMAND_OUTPUT_LENGTH = 4000;

    @Resource
    private CosManager cosManager;

    @Value("${mermaid.cli-path:mmdc}")
    private String mermaidCliPath;

    @Value("${mermaid.timeout-seconds:60}")
    private long mermaidTimeoutSeconds;

    @Value("${mermaid.browser-executable-path:}")
    private String browserExecutablePath;

    @Tool("将 Mermaid 代码转换为架构图图片，用于展示系统结构和技术关系")
    public List<ImageResource> generateMermaidDiagram(@P("Mermaid 图表代码") String mermaidCode,
                                                      @P("架构图描述") String description) {
        if (StrUtil.isBlank(mermaidCode)) {
            return new ArrayList<>();
        }
        File diagramFile = null;
        try {
            // 转换为SVG图片
            diagramFile = convertMermaidToSvg(mermaidCode);
            // 上传到COS
            String keyName = String.format("/mermaid/%s/%s",
                    RandomUtil.randomString(5), diagramFile.getName());
            String cosUrl = cosManager.uploadFile(keyName, diagramFile);
            if (StrUtil.isNotBlank(cosUrl)) {
                return Collections.singletonList(ImageResource.builder()
                        .category(ImageCategoryEnum.ARCHITECTURE)
                        .description(description)
                        .url(cosUrl)
                        .build());
            }
        } catch (Exception e) {
            log.error("生成架构图失败: {}", e.getMessage(), e);
        } finally {
            if (diagramFile != null) {
                FileUtil.del(diagramFile);
            }
        }
        return new ArrayList<>();
    }

    /**
     * 将Mermaid代码转换为SVG图片
     */
    private File convertMermaidToSvg(String mermaidCode) {
        File tempInputFile = FileUtil.createTempFile("mermaid_input_", ".mmd", true);
        File tempOutputFile = FileUtil.createTempFile("mermaid_output_", ".svg", true);
        File commandOutputFile = FileUtil.createTempFile("mermaid_cli_", ".log", true);
        File puppeteerConfigFile = null;
        boolean generated = false;

        try {
            FileUtil.writeUtf8String(mermaidCode, tempInputFile);
            puppeteerConfigFile = createPuppeteerConfigFile();
            List<String> command = buildCommand(tempInputFile, tempOutputFile, puppeteerConfigFile);
            log.info("执行 Mermaid CLI: {}", String.join(" ", command));

            Process process = new ProcessBuilder(command)
                    .redirectErrorStream(true)
                    .redirectOutput(commandOutputFile)
                    .start();
            boolean completed = process.waitFor(mermaidTimeoutSeconds, TimeUnit.SECONDS);
            if (!completed) {
                process.destroyForcibly();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                        String.format("Mermaid CLI 执行超时（%d 秒）", mermaidTimeoutSeconds));
            }

            String commandOutput = readCommandOutput(commandOutputFile);
            if (process.exitValue() != 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                        String.format("Mermaid CLI 执行失败，退出码：%d，输出：%s",
                                process.exitValue(), commandOutput));
            }
            if (!tempOutputFile.exists() || tempOutputFile.length() == 0) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                        "Mermaid CLI 未生成有效的 SVG 文件，输出：" + commandOutput);
            }
            generated = true;
            return tempOutputFile;
        } catch (IOException e) {
            throw buildExecutionException("无法启动 Mermaid CLI，请检查 mermaid.cli-path 和 PATH 配置", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw buildExecutionException("Mermaid CLI 执行被中断", e);
        } finally {
            FileUtil.del(tempInputFile);
            FileUtil.del(commandOutputFile);
            if (puppeteerConfigFile != null) {
                FileUtil.del(puppeteerConfigFile);
            }
            if (!generated) {
                FileUtil.del(tempOutputFile);
            }
        }
    }

    private List<String> buildCommand(File inputFile, File outputFile, File puppeteerConfigFile) {
        List<String> command = new ArrayList<>();
        if (SystemUtil.getOsInfo().isWindows()) {
            // Windows 的 mmdc 是 .cmd 脚本，需要通过 cmd.exe 执行。
            command.add("cmd.exe");
            command.add("/d");
            command.add("/c");
            command.add("call");
        }
        command.add(mermaidCliPath);
        command.add("-i");
        command.add(inputFile.getAbsolutePath());
        command.add("-o");
        command.add(outputFile.getAbsolutePath());
        command.add("-b");
        command.add("transparent");
        if (puppeteerConfigFile != null) {
            command.add("-p");
            command.add(puppeteerConfigFile.getAbsolutePath());
        }
        return command;
    }

    private File createPuppeteerConfigFile() {
        String executablePath = resolveBrowserExecutablePath();
        if (StrUtil.isBlank(executablePath)) {
            log.warn("未找到可用的系统 Chrome，将使用 Puppeteer 默认浏览器");
            return null;
        }

        File configFile = FileUtil.createTempFile("mermaid_puppeteer_", ".json", true);
        String configJson = JSONUtil.createObj()
                .set("executablePath", executablePath)
                .set("headless", true)
                .toStringPretty();
        FileUtil.writeUtf8String(configJson, configFile);
        log.info("Mermaid CLI 使用浏览器: {}", executablePath);
        return configFile;
    }

    private String resolveBrowserExecutablePath() {
        if (StrUtil.isNotBlank(browserExecutablePath)) {
            File configuredBrowser = new File(browserExecutablePath);
            if (!configuredBrowser.isFile()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,
                        "配置的 Mermaid 浏览器不存在：" + browserExecutablePath);
            }
            return configuredBrowser.getAbsolutePath();
        }
        if (!SystemUtil.getOsInfo().isWindows()) {
            return null;
        }

        List<File> candidates = new ArrayList<>();
        addChromeCandidate(candidates, System.getenv("PROGRAMFILES"));
        addChromeCandidate(candidates, System.getenv("PROGRAMFILES(X86)"));
        String localAppData = System.getenv("LOCALAPPDATA");
        if (StrUtil.isNotBlank(localAppData)) {
            candidates.add(new File(localAppData, "Google/Chrome/Application/chrome.exe"));
        }
        return candidates.stream()
                .filter(File::isFile)
                .map(File::getAbsolutePath)
                .findFirst()
                .orElse(null);
    }

    private void addChromeCandidate(List<File> candidates, String programFilesDirectory) {
        if (StrUtil.isNotBlank(programFilesDirectory)) {
            candidates.add(new File(programFilesDirectory, "Google/Chrome/Application/chrome.exe"));
        }
    }

    private String readCommandOutput(File commandOutputFile) {
        String output = StrUtil.trim(FileUtil.readUtf8String(commandOutputFile));
        if (StrUtil.isBlank(output)) {
            return "<无输出>";
        }
        return StrUtil.maxLength(output, MAX_COMMAND_OUTPUT_LENGTH);
    }

    private BusinessException buildExecutionException(String message, Exception cause) {
        BusinessException exception = new BusinessException(ErrorCode.SYSTEM_ERROR,
                message + "，原因：" + cause.getMessage());
        exception.initCause(cause);
        return exception;
    }
}
