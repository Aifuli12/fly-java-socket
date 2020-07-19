package com.fly.socket.nio.chat;


import com.fly.socket.nio.NioSingleThread;
import com.fly.socket.nio.chat.model.ChatPushDTO;
import com.fly.socket.nio.chat.service.IChatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:38
 */
@RestController
@Api("聊天")
public class SocketBioController {

    @Resource
    private IChatService iChatService;

    @Resource
    private NioSingleThread nioSingleThread;

    @PostMapping("/chat/push")
    @ApiOperation("推送特定用户消息")
    public String chatPushMessage(@RequestBody ChatPushDTO chatPushDTO) {
        iChatService.chatPushMessage(chatPushDTO);
        return "success";
    }

    @PostMapping("/chat/push9998")
    @ApiOperation("推送到 9998 特定用户消息")
    public String chatPushMessage2(@RequestBody ChatPushDTO chatPushDTO) {
        nioSingleThread.writeMessage(chatPushDTO);
        return "success";
    }

}
