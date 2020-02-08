package com.domiyi.weixin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@ServletComponentScan(basePackages = "com.domiyi.weixin.servlet")
@EnableJms //启动消息队列
public class WeixinApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeixinApplication.class, args);
    }

}
