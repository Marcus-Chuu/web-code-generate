package com.marcuschu.webcodegenerate.service;

import com.marcuschu.webcodegenerate.model.entity.User;
import com.marcuschu.webcodegenerate.model.enums.ChatHistoryMessageTypeEnum;
import com.marcuschu.webcodegenerate.model.request.chathistory.ChatHistoryQueryRequest;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.marcuschu.webcodegenerate.model.entity.ChatHistory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

import java.time.LocalDateTime;

/**
 * 对话历史 服务层
 *
 * @author MarcusChu
 */
public interface ChatHistoryService extends IService<ChatHistory> {


    /**
     * 保存一条对话消息
     */
    ChatHistory saveMessage(Long appId, Long userId, String message, ChatHistoryMessageTypeEnum messageType);



    /**
     * 保存一条对话消息
     */
    ChatHistory saveMessage(Long appId, Long userId, String message, ChatHistoryMessageTypeEnum messageType, Long parentId);



    /**
     * 按游标查询应用对话历史
     * @param appId             应用 ID
     * @param pageSize          分页大小
     * @param lastCreateTime    最新对话时间
     * @param loginUser         登录用户
     * @return Page<ChatHistory>
     */
    Page<ChatHistory> listAppChatHistoryByPage(Long appId, int pageSize, LocalDateTime lastCreateTime, User loginUser);



    /**
     * 构造查询对话历史的包装类
     * @param chatHistoryQueryRequest 请求类
     * @return QueryWrapper
     */
    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest chatHistoryQueryRequest);



    /**
     * 删除某个应用的全部对话历史
     */
    boolean removeByAppId(Long appId);



    /**
     * 对话记忆初始化时, 从数据库加载对话历史到记忆中
     * @param appId 应用 ID
     * @param chatMemory 对话历史
     * @param maxCount 最大加载数
     * @return int
     */
    int loadChatHistoryToMemory(Long appId, MessageWindowChatMemory chatMemory, int maxCount);

}
