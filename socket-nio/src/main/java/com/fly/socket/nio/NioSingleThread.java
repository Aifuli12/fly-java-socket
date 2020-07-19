package com.fly.socket.nio;

import com.fly.socket.nio.chat.model.ChatPushDTO;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author 张攀钦
 * @date 2020-07-19-16:32
 */
@Slf4j
public class NioSingleThread implements AutoCloseable {
    private static final String EOF = "exit";
    private static final Map<Integer, SocketChannel> MAP = new HashMap<>(16);
    private static final ConcurrentLinkedDeque<ChatPushDTO> QUEUE = new ConcurrentLinkedDeque<>();
    final ByteBuffer byteBuffer = ByteBuffer.allocateDirect(1024);
    private int port;
    private int backlog;
    private ServerSocketChannel open;
    private boolean closed = false;

    public ServerSocketChannel getOpen() {
        return open;
    }

    public NioSingleThread(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;
        try {
            open = ServerSocketChannel.open();
            // 设置使用 NIO 模型, ServerSocketChannel.accept 时候不阻塞
            open.configureBlocking(false);
            open.bind(new InetSocketAddress(port), backlog);
            this.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * @Bean(destroyMethod = "close")
     * public NioSingleThread nioSingleThread() {
     * return new NioSingleThread(9998, 20);
     * }
     */

    @Override
    public void close() throws IOException {
        closed = true;
        if (Objects.nonNull(open)) {
            if (!open.socket().isClosed()) {
                open.close();
                log.info("关闭客户端了");
            }
        }
    }

    private void init() {
        new Thread(
                () -> {
                    Integer clientIdAuto = 1;
                    while (true) {
                        if (closed) {
                            if (open.socket().isClosed()) {
                                try {
                                    open.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            return;
                        }
                        try {
                            // 处理新的客户端链接建立
                            final SocketChannel accept = open.accept();
                            if (Objects.nonNull(accept)) {
                                accept.configureBlocking(false);
                                MAP.put(clientIdAuto, accept);
                                clientIdAuto++;
                            }

                            // 处理读取事件
                            MAP.forEach((clientId, client) -> {
                                if (!client.socket().isClosed()) {
                                    byteBuffer.clear();
                                    try {
                                        final int read = client.read(byteBuffer);
                                        if (read == -1) {
                                            client.close();
                                            MAP.remove(clientId);
                                        }
                                        if (read > 0) {
                                            byteBuffer.flip();
                                            final String s = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                                            log.info("读取客户端 clientId: {} 到的数据: {}", clientId, s);
                                            if (s.contains(EOF)) {
                                                if (!client.socket().isClosed()) {
                                                    client.close();
                                                }
                                            }
                                        }

                                    } catch (IOException e) {
                                        log.error("读取数据异常,clientId: {}", clientId);
                                    }
                                }

                            });

                            // 处理写事件
                            while (!QUEUE.isEmpty()) {
                                final ChatPushDTO peek = QUEUE.remove();
                                if (Objects.isNull(peek)) {
                                    break;
                                }
                                final Integer chatId = peek.getChatId();
                                final String message = peek.getMessage();
                                final SocketChannel socketChannel = MAP.get(chatId);
                                if (Objects.isNull(socketChannel) || socketChannel.socket().isClosed()) {
                                    continue;
                                }

                                byteBuffer.clear();
                                byteBuffer.put(message.getBytes(StandardCharsets.UTF_8));
                                byteBuffer.flip();
                                socketChannel.write(byteBuffer);

                            }


                        } catch (IOException e) {
                            throw new RuntimeException("服务端异常", e);
                        }
                    }
                }, "NioSingleThread"
        ).start();

    }


    public void writeMessage(ChatPushDTO chatPushDTO) {
        Objects.requireNonNull(chatPushDTO);
        QUEUE.add(chatPushDTO);
    }
}
