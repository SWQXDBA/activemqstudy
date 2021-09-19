package com.example.activemqstudy;

import org.apache.activemq.command.ActiveMQQueue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

@SpringBootTest
public class SpringbootActive {
    @Autowired
    private JmsTemplate template;
    @Qualifier("queue")
    ActiveMQQueue queue;

    @Test
    void contextLoads() {
        System.out.println(template);
    }


}
