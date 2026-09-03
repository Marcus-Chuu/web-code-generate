package com.marcuschu.webcodegenerate.service;

import com.marcuschu.webcodegenerate.model.request.app.AppAddRequest;
import com.marcuschu.webcodegenerate.model.request.app.AppQueryRequest;
import com.marcuschu.webcodegenerate.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.marcuschu.webcodegenerate.model.entity.App;
import reactor.core.publisher.Flux;
import com.marcuschu.webcodegenerate.model.entity.User;

import java.util.List;

/**
 * 应用 服务层
 *
 * @author MarcusChu
 */
public interface AppService extends IService<App> {

    /**
     * 校验应用数据
     * @param app 应用
     * @param add 是否为创建校验
     */
    void validApp(App app, boolean add);

    /**
     * 获取应用查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);

    /**
     * 获取脱敏的应用视图
     */
    AppVO getAppVO(App app);


    /**
     * 获取应用封装类列表
     * @param appList 应用列表
     * @return List<AppVO>
     */
    List<AppVO> getAppVOList(List<App> appList);



    /**
     * 删除应用并清理该应用的全部对话历史
     */
    boolean removeAppById(Long appId);


    /**
     * 对话生成应用  ( langchain )
     * @param appId 应用 ID
     * @param message
     * @param loginUser
     * @return Flux<String>
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);


    /**
     * 应用聊天生成代码 ( langgraph )
     * @param appId   应用 ID
     * @param message 用户消息
     * @param loginUser 登录用户
     * @param agent 是否启用 Agent 模式
     * @return 生成的代码流
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser, boolean agent);


    /**
     * 部署服务
     * @param appId 应用 ID
     * @param loginUser 登录用户
     * @return String
     */
    String deployApp(Long appId, User loginUser);


    /**
     * 异步生成应用截图并更新封面
     * @param appId  应用ID
     * @param appUrl 应用访问URL
     */
    void generateAppScreenshotAsync(Long appId, String appUrl);


    /**
     * 智能路由
     * @param appAddRequest 创建应用请求
     * @param loginUser 登录人
     * @return Long
     */
    Long createApp(AppAddRequest appAddRequest, User loginUser);


}
