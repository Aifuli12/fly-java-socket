package com.fly.socket.nio.chat.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:50
 */
@Slf4j
public class SocketNioClient implements Runnable, AutoCloseable {

    private int clientId;

    private SocketChannel client;

    private String EOF = "exit";


    public int getClientId() {
        return clientId;
    }

    public SocketNioClient(int clientId, SocketChannel client) {
        Objects.requireNonNull(client);
        this.client = client;
        this.clientId = clientId;
    }

    public boolean isClosed() {
        return this.client.socket().isClosed();
    }


    @Override
    public void run() {
        int length = 0;
        String s = "";
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
            // NIO 这里不会阻塞,读不到设局直接返回数据
            while (true) {
                length = this.client.read(byteBuffer);
                if (length > 0) {
                    byteBuffer.flip();
                    s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                    log.info("接收到客户端的消息,clientId: {} ,message: {}", clientId, s);
                    if (s.contains(EOF)) {
                        this.close();
                        return;
                    }
                }
                if (length == -1) {
                    log.info("客户端关闭了,clientId: {},服务端释放资源", clientId);
                    this.close();
                    return;
                }
                // 可以在读不到的数据时候,处理写入数据
//                Thread.sleep(1000);
//                log.info("读数据循环一轮");
            }
        } catch (IOException e) {
            if (length == -1) {
                this.close();
            }
        }
    }

    @Override
    public void close() {
        try {
            if (!this.isClosed()) {
                client.close();
                SpringContextUtil.pushSocketClientClose(new SocketClientCloseEvent(clientId));
            }
        } catch (IOException e) {
            log.error("关流 socket,{}", clientId, e);
        } finally {
            log.info("关闭客户端,clientId: {}", clientId);
        }

    }

    public void writeMessage(String message) {
        Objects.requireNonNull(message);
        try {
            ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
            this.client.write(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
        } catch (IOException e) {
            log.error("写入到客户端: {} 失败, message: {}", clientId, message);
        } finally {
            log.info("写入到客户端: {} 结束, message: {}", clientId, message);
        }
    }
}
