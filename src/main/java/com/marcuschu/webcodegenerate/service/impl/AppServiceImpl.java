package com.marcuschu.webcodegenerate.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.marcuschu.webcodegenerate.ai.AiCodeGenTypeRoutingService;
import com.marcuschu.webcodegenerate.ai.AiCodeGenTypeRoutingServiceFactory;
import com.marcuschu.webcodegenerate.ai.core.AiCodeGeneratorFacade;
import com.marcuschu.webcodegenerate.ai.core.builder.VueProjectBuilder;
import com.marcuschu.webcodegenerate.ai.core.streamHandler.StreamHandlerExecutor;
import com.marcuschu.webcodegenerate.constant.AppConstant;
import com.marcuschu.webcodegenerate.exception.BusinessException;
import com.marcuschu.webcodegenerate.exception.ErrorCode;
import com.marcuschu.webcodegenerate.exception.ThrowUtils;
import com.marcuschu.webcodegenerate.langgraph4j.CodeGenWorkflow;
import com.marcuschu.webcodegenerate.model.entity.User;
import com.marcuschu.webcodegenerate.model.enums.ChatHistoryMessageTypeEnum;
import com.marcuschu.webcodegenerate.model.enums.CodeGenTypeEnum;
import com.marcuschu.webcodegenerate.model.request.app.AppAddRequest;
import com.marcuschu.webcodegenerate.model.request.app.AppQueryRequest;
import com.marcuschu.webcodegenerate.model.vo.AppVO;
import com.marcuschu.webcodegenerate.model.vo.UserVO;
import com.marcuschu.webcodegenerate.service.ChatHistoryService;
import com.marcuschu.webcodegenerate.service.ScreenshotService;
import com.marcuschu.webcodegenerate.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.marcuschu.webcodegenerate.model.entity.App;
import com.marcuschu.webcodegenerate.mapper.AppMapper;
import com.marcuschu.webcodegenerate.service.AppService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现
 *
 * @author MarcusChu
 */
@Slf4j
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService {


    /**
     * 用户服务
     */
    @Resource
    private UserService userService;


    /**
     * 对话历史
     */
    @Resource
    private ChatHistoryService chatHistoryService;


    /**
     * AI 门面模式生成和保存代码
     */
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;


    /**
     * 流处理器
     */
    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;


    /**
     * VUE 构建工具
     */
    @Resource
    private VueProjectBuilder vueProjectBuilder;


    /**
     * 网页截图工具
     */
    @Resource
    private ScreenshotService screenshotService;


    /**
     * AI 路由工厂类
     */
    @Resource
    private AiCodeGenTypeRoutingServiceFactory aiCodeGenTypeRoutingServiceFactory;



    /**
     * 部署地址
     */
    @Value("${code.deploy-host:http://localhost}")
    private String deployHost;


    /**
     * APP 名字最大长度
     */
    private static final int MAX_APP_NAME_LENGTH = 256;

    /**
     * 应用封面地址最大长度
     */
    private static final int MAX_COVER_LENGTH = 512;

    /**
     * 提示词最大长度
     */
    private static final int MAX_INIT_PROMPT_LENGTH = 10000;


    /**
     * 应用信息字段集合
     */
    private static final Set<String> SORT_FIELD_WHITELIST = Set.of(
            "id", "appName", "codeGenType", "deployKey", "priority", "userId",
            "deployedTime", "editTime", "createTime", "updateTime"
    );

    @Override
    public void validApp(App app, boolean add) {
        ThrowUtils.throwIf(app == null, ErrorCode.PARAMS_ERROR);
        String appName = app.getAppName();
        String cover = app.getCover();
        String initPrompt = app.getInitPrompt();
        Integer priority = app.getPriority();

        if (add) {
            ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化提示词不能为空");
        }
        if (appName != null) {
            ThrowUtils.throwIf(StrUtil.isBlank(appName), ErrorCode.PARAMS_ERROR, "应用名称不能为空");
            ThrowUtils.throwIf(appName.length() > MAX_APP_NAME_LENGTH,
                    ErrorCode.PARAMS_ERROR, "应用名称过长");
        }
        if (cover != null) {
            ThrowUtils.throwIf(cover.length() > MAX_COVER_LENGTH,
                    ErrorCode.PARAMS_ERROR, "应用封面地址过长");
        }
        if (initPrompt != null) {
            ThrowUtils.throwIf(initPrompt.length() > MAX_INIT_PROMPT_LENGTH,
                    ErrorCode.PARAMS_ERROR, "初始化提示词过长");
        }
        if (priority != null) {
            ThrowUtils.throwIf(priority < 0, ErrorCode.PARAMS_ERROR, "优先级不能小于 0");
        }
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        String safeSortField = StrUtil.isNotBlank(sortField) && SORT_FIELD_WHITELIST.contains(sortField) ? sortField : "createTime";

        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(safeSortField, "ascend".equals(sortOrder));
    }


    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        User user = userService.getById(app.getUserId());
        appVO.setUser(userService.getUserVO(user));
        return appVO;
    }



    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息, 避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }




    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeAppById(Long appId) {
        ThrowUtils.throwIf(appId == null || appId <= 0,
                ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        boolean result = this.removeById(appId);
        if (result) {
            chatHistoryService.removeByAppId(appId);
        }
        return result;
    }



    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用, 仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 4. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 5. 通过校验后, 添加用户消息到对话历史
        chatHistoryService.saveMessage(appId, loginUser.getId(), message, ChatHistoryMessageTypeEnum.USER);
        // 6. 调用 AI 生成代码 (流式)
        Flux<String> codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        // 7. 收集 AI 响应内容并在完成后记录到对话历史
        return streamHandlerExecutor.doExecute(codeStream, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }



    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser, boolean agent) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "用户消息不能为空");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限访问该应用, 仅本人可以生成代码
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限访问该应用");
        }
        // 4. 获取应用的代码生成类型
        String codeGenTypeStr = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenTypeStr);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 5. 通过校验后，添加用户消息到对话历史
        chatHistoryService.saveMessage(appId, loginUser.getId(), message, ChatHistoryMessageTypeEnum.USER, null);
        // 6. 根据 agent 参数选择生成方式
        Flux<String> codeStream;
        if (agent) {
            // Agent 模式: 使用工作流生成代码
            codeStream = new CodeGenWorkflow().executeWorkflowWithFlux(message, appId);
        } else {
            // 传统模式：调用 AI 生成代码（流式）
            codeStream = aiCodeGeneratorFacade.generateAndSaveCodeStream(message, codeGenTypeEnum, appId);
        }
        // 7. 收集 AI 响应内容并在完成后记录到对话历史
        return streamHandlerExecutor.doExecute(codeStream, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }




    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1. 参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用 ID 不能为空");
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR, "用户未登录");
        // 2. 查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3. 验证用户是否有权限部署该应用, 仅本人可以部署
        if (!app.getUserId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署该应用");
        }
        // 4. 检查是否已有 deployKey
        String deployKey = app.getDeployKey();
        // 没有则生成 6 位 deployKey (大小写字母 + 数字)
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }
        // 5. 获取代码生成类型, 构建源目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 6. 检查源目录是否存在
        File sourceDir = new File(sourceDirPath);
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在, 请先生成代码");
        }
        // 7. Vue 项目特殊处理 : 执行构建
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // Vue 项目需要构建
            boolean buildSuccess = vueProjectBuilder.buildProject(sourceDirPath);
            ThrowUtils.throwIf(!buildSuccess, ErrorCode.SYSTEM_ERROR, "Vue 项目构建失败，请检查代码和依赖");
            // 检查 dist 目录是否存在
            File distDir = new File(sourceDirPath, "dist");
            ThrowUtils.throwIf(!distDir.exists(), ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成 dist 目录");
            // 将 dist 目录作为部署源
            sourceDir = distDir;
            log.info("Vue 项目构建成功, 将部署 dist 目录: {}", distDir.getAbsolutePath());
        }
        // 8. 复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "部署失败 : " + e.getMessage());
        }
        // 9. 更新应用的 deployKey 和部署时间
        App updateApp = new App();
        updateApp.setId(appId);
        updateApp.setDeployKey(deployKey);
        updateApp.setDeployedTime(LocalDateTime.now());
        boolean updateResult = this.updateById(updateApp);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 10. 返回可访问的 URL
        String normalizedDeployBaseUrl = deployHost.replaceAll("/+$", "");
        String appDeployUrl = String.format("%s/%s/", normalizedDeployBaseUrl, deployKey);
        // 11. 异步生成截图并更新应用封面
        generateAppScreenshotAsync(appId, appDeployUrl);
        return appDeployUrl;
    }






    /**
     * 异步生成应用截图并更新封面
     * @param appId  应用 ID
     * @param appUrl 应用访问 URL
     */
    @Override
    public void generateAppScreenshotAsync(Long appId, String appUrl) {
        // 使用虚拟线程异步执行
        Thread.startVirtualThread(() -> {
            // 调用截图服务生成截图并上传
            String screenshotUrl = screenshotService.generateAndUploadScreenshot(appUrl);
            // 更新应用封面字段
            App updateApp = new App();
            updateApp.setId(appId);
            updateApp.setCover(screenshotUrl);
            boolean updated = this.updateById(updateApp);
            ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "更新应用封面字段失败");
        });
    }





    /**
     * 删除应用时关联删除对话历史
     * @param id 应用 ID
     * @return 是否成功
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        // 转换为 Long 类型
        Long appId = Long.valueOf(id.toString());
        if (appId <= 0) {
            return false;
        }
        // 先删除关联的对话历史
        try {
            chatHistoryService.removeByAppId(appId);
        } catch (Exception e) {
            // 记录日志但不阻止应用删除
            log.error("删除应用关联对话历史失败: {}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }






    @Override
    public Long createApp(AppAddRequest appAddRequest, User loginUser) {
        // 参数校验
        String initPrompt = appAddRequest.getInitPrompt();
        ThrowUtils.throwIf(StrUtil.isBlank(initPrompt), ErrorCode.PARAMS_ERROR, "初始化 prompt 不能为空");

        // 构造入库对象
        App app = new App();
        BeanUtil.copyProperties(appAddRequest, app);
        app.setUserId(loginUser.getId());
        // 应用名称暂时为 initPrompt 前 12 位
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));

        // 使用 AI 智能选择代码生成类型（多例模式）
        AiCodeGenTypeRoutingService routingService = aiCodeGenTypeRoutingServiceFactory.createAiCodeGenTypeRoutingService();
        CodeGenTypeEnum selectedCodeGenType = routingService.routeCodeGenType(initPrompt);
        app.setCodeGenType(selectedCodeGenType.getValue());

        // 插入数据库
        boolean result = this.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        log.info("应用创建成功, ID: {}, 类型: {}", app.getId(), selectedCodeGenType.getValue());
        return app.getId();
    }


}
