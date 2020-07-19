package com.fly.socket.bio.chat.bio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:50
 */
@Slf4j
public class SocketBioClient implements Runnable, AutoCloseable {

    private int clientId;

    private Socket client;

    private String EOF = "exit";

    public int getClientId() {
        return clientId;
    }

    public SocketBioClient(int clientId, Socket client) {
        Objects.requireNonNull(client);
        this.client = client;
        this.clientId = clientId;
    }

    public boolean isClosed() {
        return this.client.isClosed();
    }

    private InputStream getInputStreamBySocket() {
        Objects.requireNonNull(client);
        try {
            if (!this.client.isClosed()) {
                return this.client.getInputStream();
            }
        } catch (IOException e) {
            log.error("获取 socket 读取失败", e);
            throw new RuntimeException(e);
        }
        return null;
    }

    private OutputStream getOutputStreamByClient() {
        Objects.requireNonNull(client);
        try {
            if (!this.client.isClosed()) {
                return this.client.getOutputStream();
            }
        } catch (IOException e) {
            log.error("获取 socket 写入失败", e);
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public void run() {
        final InputStream inputStreamBySocket = this.getInputStreamBySocket();
        byte[] data = new byte[1024];
        int length = 0;
        String s = "";
        try {
            while ((length = inputStreamBySocket.read(data)) >= 0) {
                s = new String(data, 0, length, StandardCharsets.UTF_8);
                if (s.contains(EOF)) {
                    this.close();
                    return;
                }
                log.info("接收到客户端的消息,clientId: {} ,message: {}", clientId, s);

            }
            if (length == -1) {
                log.info("客户端关闭了,clientId: {},服务端释放资源", clientId);
                this.close();
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
            if (!client.isClosed()) {
                client.close();
                SpringContextUtil.pushSocketClientClose(new SocketClientCloseEvent(clientId));
            }
        } catch (IOException e) {
            log.error("关流失败,{}", clientId, e);
        } finally {
            log.info("关闭客户端,clientId: {}", clientId);
        }

    }

    public void writeMessage(String message) {
        Objects.requireNonNull(message);
        try {
            this.getOutputStreamByClient().write(message.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            log.error("写入到客户端: {} 失败, message: {}", clientId, message);
        } finally {
            log.info("写入到客户端: {} 结束, message: {}", clientId, message);
        }
    }
}
