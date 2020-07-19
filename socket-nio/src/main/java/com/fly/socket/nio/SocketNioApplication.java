package com.fly.socket.nio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author zhangpanqin
 */
@SpringBootApplication
@EnableOpenApi
@EnableAsync(proxyTargetClass = true)
public class SocketNioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketNioApplication.class, args);
    }

    @Bean(destroyMethod = "close")
    public NioSingleThread nioSingleThread() {
        return new NioSingleThread(9998, 20);
    }
}
