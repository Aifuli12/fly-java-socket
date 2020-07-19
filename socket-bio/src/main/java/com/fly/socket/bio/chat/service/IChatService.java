package com.fly.socket.bio.chat.service;

import com.fly.socket.bio.chat.model.ChatPushDTO;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:42
 */
public interface IChatService {
    /**
     * 推送给特定的用户消息
     *
     * @param chatPushDTO
     * @author 张攀钦
     * @title chatPushMessage
     */
    void chatPushMessage(ChatPushDTO chatPushDTO);
}
