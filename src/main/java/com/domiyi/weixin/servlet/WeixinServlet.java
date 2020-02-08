package com.domiyi.weixin.servlet;

import com.domiyi.weixin.util.CheckUtil;
import com.domiyi.weixin.util.MessageUtil;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.dom4j.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.handler.annotation.SendTo;

import javax.jms.Queue;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * Created by  OSCAR on 2018/12/19
 * 验证微信token
 */
@WebServlet("/vv")
public class WeixinServlet extends HttpServlet {
    @Autowired
    private Queue queue;

    //注入springboot封装的工具类
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    private String result = "0";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String signature = req.getParameter("signature");
        String timestamp = req.getParameter("timestamp");
        String nonce = req.getParameter("nonce");
        String echostr = req.getParameter("echostr");
        System.out.println("123");
        //将response打印输出
        PrintWriter out = resp.getWriter();
        try {
            if (CheckUtil.checkSignature(signature, timestamp, nonce)) {
                out.println(echostr);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public static JSONObject doGetStr(String url) throws ParseException, IOException {
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        JSONObject jsonObject = null;
        HttpResponse httpResponse = client.execute(httpGet);
        HttpEntity entity = httpResponse.getEntity();
        if (entity != null) {
            String result = EntityUtils.toString(entity, "UTF-8");
            jsonObject = JSONObject.fromObject(result);
        }
        return jsonObject;
    }

    /**
     * 1value里面真的有值么？--因为这是发送消息，所以发送人，接收人，时间，内容是固定的，所以可以获得value
     * 2
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        System.out.println(req.toString() + "--------");
        //把微信消息返回给客户端
        PrintWriter out = resp.getWriter();
        try {
            Map<String, String> map = MessageUtil.xmlToMap(req);
            System.out.println(map);
            String fromUserName = map.get("FromUserName");
            String toUserName = map.get("ToUserName");
            String msgType = map.get("MsgType");
            String content = map.get("Content");

            String message = null;
            if (MessageUtil.MESSAGE_TEXT.equals(msgType)) {
                //按照关键字进行回复d
                System.out.println(content);
                System.out.println(toUserName);
                System.out.println(fromUserName);
                JSONObject jsonObject = doGetStr("https://www.myznsh.com/searchcsdn?wd=" + content);
                System.out.println(jsonObject.getString("data"));
                //方法一：添加消息到消息队列
                //jmsMessagingTemplate.convertAndSend(queue, content);
                //方法二：这种方式不需要手动创建queue，系统会自行创建名为test的队列

                jmsMessagingTemplate.convertAndSend("test", content);
                //延迟2秒钟,result应该就有值了
                Thread.sleep(2000);
                /*message = MessageUtil.initText(toUserName,fromUserName,jsonObject.getString("data").substring(1,500));*/
                message = MessageUtil.initText(toUserName, fromUserName, result);
                /* if ("1".equals(content)){
                 *//*message = MessageUtil.initText(toUserName,fromUserName,MessageUtil.firstMenu());*//*
                 *//*message = MessageUtil.initText(toUserName,fromUserName,"Hello,World");*//*
                    JSONObject jsonObject = doGetStr("https://www.myznsh.com/searchcsdn?wd=%E7%88%B1%E6%83%85");
                    System.out.println(jsonObject.getString("data"));


                    *//*message = MessageUtil.initText(toUserName,fromUserName,jsonObject.getString("data").substring(1,500));*//*
                    message = MessageUtil.initText(toUserName,fromUserName,content);

                }else if ("2".equals(content)){
                    message = MessageUtil.initText(toUserName,fromUserName,MessageUtil.secondMenu());
                }else  if ("?".equals(content) || "?".equals(content)){
                    message = MessageUtil.initText(toUserName,fromUserName,MessageUtil.menuText());
                }*/

                //以下是做和微信编辑模式下一样的样式
            } else if (MessageUtil.MESSAGE_EVENT.equals(msgType)) {
                String eventType = map.get("Event");//可以获取到事件类型
                if (MessageUtil.MESSAGE_SUBSCRIBE.equals(eventType)) {//关注以后回复主菜单
                    message = MessageUtil.initText(toUserName, fromUserName, MessageUtil.menuText());
                }
            }

            System.out.println();
            System.out.println(message + "00000");
            out.print(message);//把消息发送到客户端

        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }

    }

    @JmsListener(destination = "test2")
    // SendTo 会将此方法返回的数据, 写入到 OutQueue 中去.
    @SendTo("SQueue")
    public String handleMessage(String name) {
        System.out.println(name + "----------");
        result = name;
        return name;
    }


}
