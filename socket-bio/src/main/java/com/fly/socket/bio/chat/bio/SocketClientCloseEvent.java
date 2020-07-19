package com.fly.socket.bio.chat.bio;

import org.springframework.context.ApplicationEvent;
import org.springframework.lang.NonNull;

/**
 * @author 张攀钦
 * @date 2020-07-19-14:28
 */
public class SocketClientCloseEvent extends ApplicationEvent {

    public SocketClientCloseEvent(@NonNull Integer source) {
        super(source);
    }

    @Override
    public Integer getSource() {
        return (Integer) super.getSource();
    }
}
