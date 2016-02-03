package cn.sinobest.traverse.queue;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.IOException;

import static org.junit.Assert.*;

public class ConsumerTest {

    @Test
    public void test(){

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext-jms.xml");
        applicationContext.start();

        ProducerService producer = (ProducerService) applicationContext.getBean("producer");
        producer.sendMsg();

        Consumer consumer = (Consumer) applicationContext.getBean("consumer");
        consumer.receiveMsg();
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}