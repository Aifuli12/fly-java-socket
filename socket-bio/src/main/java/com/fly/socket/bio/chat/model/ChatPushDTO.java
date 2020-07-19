package com.fly.socket.bio.chat.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:43
 */
@Data
@ApiModel("聊天的消息")
public class ChatPushDTO implements Serializable {

    @ApiModelProperty("客户 id")
    private Integer chatId;


    @ApiModelProperty("发送给客户的消息")
    private String message;
}
