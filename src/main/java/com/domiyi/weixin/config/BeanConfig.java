package com.domiyi.weixin.config;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.Queue;
 //activemq配置
/*
 * @author uv
 * @date 2018/9/15 14:21
 */
@Configuration
public class BeanConfig {

    //定义存放消息的队列
    @Bean
    public Queue queue() {
        return new ActiveMQQueue("ActiveMQQueue");
    }
}