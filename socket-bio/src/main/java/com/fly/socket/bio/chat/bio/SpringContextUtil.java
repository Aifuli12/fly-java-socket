package com.fly.socket.bio.chat.bio;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.stereotype.Component;

/**
 * @author 张攀钦
 * @date 2020-07-19-14:34
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static void pushSocketClientClose(ApplicationEvent applicationEvent) {
        SpringContextUtil.applicationContext.publishEvent(applicationEvent);
    }
}
