package cn.sinobest.traverse.queue;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;
import javax.jms.JMSException;

/**
 * Created by zy-xx on 16/2/2.
 */
public class Consumer {
    private JmsTemplate template;
    private Destination destination;

    public void receiveMsg(){
        ActiveMQTextMessage message = (ActiveMQTextMessage) template.receive(destination);
        System.out.println("message = " + message.toString());
        try {
            System.out.println(message.getText());
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public JmsTemplate getTemplate() {
        return template;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

    public Destination getDestination() {
        return destination;
    }
}
