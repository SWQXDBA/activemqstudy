package com.example.activemqstudy;

import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.jms.ConnectionFactory;
import javax.jms.JMSDestinationDefinition;
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

    @RequestMapping("/send2")
    public String send2() {
        String message = UUID.randomUUID().toString();
        //  this.jmsMessagingTemplate.convertAndSend(this.queue, message);
        this.jmsMessagingTemplate.convertAndSend(this.queue, message);
        return "消息发送成功！message=" + message;
    }
/*    @JmsListener(destination = "active.queue")
    public void readActiveQueue(String message) {
        System.out.println("接受到：" + message);
    }*/

    //需要spring.jms.pub-sub-domain=true 否则只有queue的生效 反之亦然
    //这个好像知识影响接受者 照样可以发送消息给队列或者topic不受影响 但是影响到  @JmsListener接收 如果是true 那么上面的@JmsListener(destination = "active.queue")就不能接受到消息了 但是修改设置为false后重启 可以立刻接收到队列中的消息
    @JmsListener(destination = "active.topic")
    public void readActiveTopic1(String message) {
        System.out.println("1号客户端接受到：" + message);
    }

    @JmsListener(destination = "active.topic")
    public void readActiveTopic2(String message) {
        System.out.println("2号客户端接受到：" + message);
    }


}
