package com.fly.socket.bio.chat.service.impl;

import com.fly.socket.bio.chat.bio.ServerSockStartRunner;
import com.fly.socket.bio.chat.model.ChatPushDTO;
import com.fly.socket.bio.chat.service.IChatService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:42
 */
@Service
@Slf4j
public class ChatServiceImpl implements IChatService {

    @Resource
    private ServerSockStartRunner serverSockStartRunner;

    @Override
    public void chatPushMessage(ChatPushDTO chatPushDTO) {
        log.info("推送消息: {}", chatPushDTO);
        serverSockStartRunner.writeMessage(chatPushDTO.getChatId(), chatPushDTO.getMessage());
    }
}
