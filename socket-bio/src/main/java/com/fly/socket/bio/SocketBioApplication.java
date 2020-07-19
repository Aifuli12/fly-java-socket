package com.fly.socket.bio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import springfox.documentation.oas.annotations.EnableOpenApi;

/**
 * @author zhangpanqin
 */
@SpringBootApplication
@EnableOpenApi
@EnableAsync(proxyTargetClass = true)
public class SocketBioApplication {

    public static void main(String[] args) {
        SpringApplication.run(SocketBioApplication.class, args);
    }

}
