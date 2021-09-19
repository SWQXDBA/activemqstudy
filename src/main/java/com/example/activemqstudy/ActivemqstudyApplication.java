package com.example.activemqstudy;

import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.jms.Queue;

@SpringBootApplication
public class ActivemqstudyApplication {


    public static void main(String[] args) {
        SpringApplication.run(ActivemqstudyApplication.class, args);
    }

}
