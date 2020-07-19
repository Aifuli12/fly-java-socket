package com.fly.socket.bio.chat.bio;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:58
 */
@Component
@Slf4j
public class ServerSockStartRunner implements CommandLineRunner, InitializingBean {

    private ServerSocketMain serverSocketMain;

    @Override
    public void run(String... args) throws Exception {
        new Thread(serverSocketMain, "启动 server socket 的线程").start();
        log.info("socket 启动成功");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        serverSocketMain = new ServerSocketMain(9999, 5);
        log.info("初始话 server socket");
    }

    @EventListener(SocketClientCloseEvent.class)
    @Async
    public void removeClientByClientId(SocketClientCloseEvent socketClientCloseEvent) {
        final Integer source = socketClientCloseEvent.getSource();
        SocketBioClient socketBioClient = serverSocketMain.removeSocketByClientId(source);
        log.info("监听到客户端关闭了 clientId : {}", socketBioClient.getClientId());
    }

    public void writeMessage(int clientId, String message) {
        this.serverSocketMain.writeMessage(clientId, message);
    }

}
