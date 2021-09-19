package com.example.activemqstudy;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.Queue;
import javax.jms.Topic;
import java.util.UUID;

@RestController
public class Controller {

    @Bean
    public Queue queue() {
        return new ActiveMQQueue("active.queue");
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic("active.topic");
    }

    @Autowired
    Queue queue;
    @Autowired
    Topic topic;
    @Autowired
    JmsMessagingTemplate jmsMessagingTemplate;

    @RequestMapping("/send")
    public String send() {
        String message = UUID.randomUUID().toString();
        //  this.jmsMessagingTemplate.convertAndSend(this.queue, message);
        this.jmsMessagingTemplate.convertAndSend(this.topic, message);
        return "消息发送成功！message=" + message;
    }


    @JmsListener(destination = "active.queue")
    public void readActiveQueue(String message) {
        System.out.println("接受到：" + message);
    }

    @JmsListener(destination = "active.topic")
    public void readActiveTopic1(String message) {
        System.out.println("1号客户端接受到：" + message);
    }

    @JmsListener(destination = "active.topic")
    public void readActiveTopic2(String message) {
        System.out.println("2号客户端接受到：" + message);
    }
}
