package com.example.activemqstudy;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.*;
import java.io.IOException;
import java.util.ArrayList;

@SpringBootTest
class ActivemqstudyApplicationTests {

    static String url = "tcp://192.168.241.130:61616";
    static String queName = "que1";
    static String topicName = "topic01";

    @Test
    void contextLoads() {
        for (int i = 0; i < 3; i++) {
            //使用队列
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
            try {
                Connection connection = factory.createConnection();//创建连接
                connection.start();
                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                //创建Destination
                Queue queue = session.createQueue(queName);

                //创建生产者
                MessageProducer producer = session.createProducer(queue);

                //通过生产者发送消息
                for (int j = 0; j < 99999; j++) {
                    //使用session创建消息
                    TextMessage textMessage = session.createTextMessage("hello" + j);
                    producer.send(textMessage);
                }

                producer.close();
                session.close();
                connection.close();
            } catch (JMSException e) {
                e.printStackTrace();
            }


        }


    }

    @Test
    void contextLoadsConsumer() {
        //使用队列
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();//创建连接
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建Destination
            Queue queue = session.createQueue(queName);

            //创建消费者
            MessageConsumer consumer = session.createConsumer(queue);
            while (true) {

                TextMessage message = (TextMessage) consumer.receive();
                System.out.println(message.getText());
            }
            //通过生产者发送消息


//            consumer.close();
//            session.close();
//            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    @Test
    void contextLoadsConsumer2() {
        //使用队列
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();//创建连接
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            //创建Destination
            Queue queue = session.createQueue(queName);

            //创建消费者
            MessageConsumer consumer = session.createConsumer(queue);
            //注册监听器
            consumer.setMessageListener(new MessageListener() {
                @Override
                public void onMessage(Message message) {
                    try {
                        System.out.println(((TextMessage) message).getText());
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }
            });
            try {
                System.in.read();//让程序不终止 类似于while(true)
            } catch (IOException e) {
                e.printStackTrace();
            }


            consumer.close();
            session.close();
            connection.close();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }


    @Test
    void contextLoadsConsumerTopic() {//生产者
        //使用队列
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();//创建连接
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            //创建Destination
            Topic topic = session.createTopic(topicName);

            //创建生产者
            MessageProducer producer = session.createProducer(topic);

            for (int i = 0; i < 3; i++) {
                TextMessage topicMessage = session.createTextMessage("topicTextMessage" + i);
                MapMessage mapMessage = session.createMapMessage();
                mapMessage.setString("per","name"+i);
                if(i==2){
                    mapMessage.setStringProperty("level","vip");
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
    void contextLoadsConsumerTopic2() {//topic的消费者
        //使用队列
        ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(url);
        try {
            Connection connection = factory.createConnection();//创建连接
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);


            //创建Destination
            Topic topic = session.createTopic(topicName);

            //创建消费者
            MessageConsumer consumer = session.createConsumer(topic);
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
                        String person =  mapMessage.getString("per");
                        System.out.println(person);

                        if(mapMessage.propertyExists("level")){

                            System.out.println(mapMessage.getStringProperty("level"));
                        }
                    } catch (JMSException e) {
                        e.printStackTrace();
                    }
                }else if(message instanceof ObjectMessage){
                    ObjectMessage mapMessage = (ObjectMessage) message;
                    try {
                        Object o =  mapMessage.getObject();
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
