package com.fly.socket.nio.chat.nio;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 张攀钦
 * @date 2020-07-18-23:51
 */
@Slf4j
public class ServerSocketMain implements Runnable {

    private final int backlog;
    private final int port;
    private ServerSocketChannel serverSocket;

    private static final AtomicInteger CLIENT_ID = new AtomicInteger();

    private static final ConcurrentHashMap<Integer, SocketNioClient> CLIENT = new ConcurrentHashMap(16);


    private ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(20, 50, 60, TimeUnit.SECONDS, new LinkedBlockingDeque<>(2000));

    public ServerSocketMain(int port, int backlog) {

        this.port = port;
        this.backlog = backlog;
        intServerSocket();
    }

    private void intServerSocket() {
        try {
            this.serverSocket = ServerSocketChannel.open();
            // ServerSocketChannel.accept() 不阻塞
            this.serverSocket.configureBlocking(false);
            this.serverSocket.bind(new InetSocketAddress(port), backlog);
        } catch (IOException e) {
            log.error("serverSocket 初始化失败", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                final SocketChannel accept = serverSocket.accept();
                if (Objects.nonNull(accept)) {
                    accept.configureBlocking(false);
                    final int currentIdClient = CLIENT_ID.incrementAndGet();
                    final SocketNioClient socketNioClient = new SocketNioClient(currentIdClient, accept);
                    CLIENT.put(currentIdClient, socketNioClient);
                    new Thread(socketNioClient, "客户端-" + currentIdClient).start();
                }

            } catch (IOException e) {
                log.info("接受客户端你失败", e);
            }
        }
    }

    public void writeMessage(Integer clientId, String message) {
        Objects.requireNonNull(clientId);
        Objects.requireNonNull(message);
        final SocketNioClient socketNioClient = CLIENT.get(clientId);
        Optional.ofNullable(socketNioClient).orElseThrow(() -> new RuntimeException("clientId: " + clientId + " 不合法"));
        threadPoolExecutor.execute(() -> {
            if (socketNioClient.isClosed()) {
                CLIENT.remove(clientId);
                return;
            }
            socketNioClient.writeMessage(message);
        });
    }

    public SocketNioClient removeSocketByClientId(Integer clientId) {
        Objects.requireNonNull(clientId);
        final SocketNioClient socketNioClient = CLIENT.get(clientId);
        Optional.ofNullable(socketNioClient).orElseThrow(() -> new RuntimeException("clientId 不合法"));
        if (socketNioClient.isClosed()) {
            return CLIENT.remove(clientId);
        }
        throw new RuntimeException("客户端没有关闭不能移除");
    }
}
