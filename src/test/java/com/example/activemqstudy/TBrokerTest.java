package com.example.activemqstudy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.usage.MemoryUsage;
import org.apache.activemq.usage.SystemUsage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.*;
import java.io.IOException;
import java.util.LinkedList;

@SpringBootTest
public class TBrokerTest {
    static String url = "tcp://localhost:61616";
    static String queName = "que1";
    static String topicName = "topic01";

    @Test
    void contextLoads() {
        BrokerService service = new BrokerService();
        service.setUseJmx(true);
        try {
            service.addConnector("tcp://localhost:61616");

            //默认需要102400mb磁盘空间 太多了 进行设置一下
            SystemUsage systemUsage = new SystemUsage();
            MemoryUsage memoryUsage = new MemoryUsage();
            memoryUsage.setLimit(10000);
            systemUsage.setMemoryUsage(memoryUsage);
            service.setSystemUsage(systemUsage);

            service.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

  /*      需要添加依赖    <dependency>
        <groupId>org.apache.activemq</groupId>
        <artifactId>activemq-kahadb-store</artifactId>
        <scope>runtime</scope>
    </dependency>*/
    }


    @Test
    void contextLoadsConsumerTopic订阅发布() {//生产者
        //使用队列
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();//创建连接

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            //创建Destination
            Topic topic = session.createTopic(topicName);


            //创建生产者
            MessageProducer producer = session.createProducer(topic);
            //设置持久化模式
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);

            connection.start();
            for (int i = 0; i < 3; i++) {
                TextMessage topicMessage = session.createTextMessage("topicTextMessage" + i);
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("per", "name" + i);
                if (i == 2) {
                    mapMessage.setStringProperty("level", "vip");
                }
                producer.send(mapMessage);
                producer.send(topicMessage);
            }

            //注册监听器

            producer.close();
            session.close();
            connection.close();
        } catch (JMSException jmsException) {
            jmsException.printStackTrace();
        }


    }

    @Test
    void contextLoadsConsumerTopic2订阅发布() {//topic的消费者
        //使用队列
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();//创建连接
            connection.setClientID("s1");
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            //创建Destination
            Topic topic = session.createTopic(topicName);

            //创建消费者
            TopicSubscriber consumer = session.createDurableSubscriber(topic, "备注");


            connection.start();
            //注册监听器
            consumer.setMessageListener(message -> {

                if (null == message)
                    return;
                if (message instanceof TextMessage) {
                    TextMessage textMessage = (TextMessage) message;
                    try {
                        System.out.println(textMessage.getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } else if (message instanceof MapMessage) {
                    MapMessage mapMessage = (MapMessage) message;
                    try {
                        String person = mapMessage.getString("per");
                        System.out.println(person);

                        if (mapMessage.propertyExists("level")) {

                            System.out.println(mapMessage.getStringProperty("level"));
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                } else if (message instanceof ObjectMessage) {
                    ObjectMessage mapMessage = (ObjectMessage) message;
                    try {
                        Object o = mapMessage.getObject();
                        System.out.println(o);
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }


            });


            System.in.read();
            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException | IOException jmsException) {
            jmsException.printStackTrace();
        }


    }
}
