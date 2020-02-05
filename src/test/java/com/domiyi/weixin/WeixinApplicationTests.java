package com.domiyi.weixin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest
public class WeixinApplicationTests {

    @Test
    public void contextLoads() throws IOException {
        System.out.println(new Date().getTime());
        /*System.out.println(System.currentTimeMillis());
        AccessToken accessToken = WeixinUtil.getAccessToken();
        System.out.println(accessToken);*/
    }

}
