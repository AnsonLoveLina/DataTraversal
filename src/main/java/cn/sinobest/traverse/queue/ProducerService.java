package cn.sinobest.traverse.queue;

import org.springframework.jms.core.JmsTemplate;

import javax.jms.Destination;

/**
 * Created by zy-xx on 16/2/2.
 */
public class ProducerService {
    private JmsTemplate template;
    private Destination destination;
    private String messageCount;

    public void sendMsg(){
        template.convertAndSend(destination,"hehe");
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

    public void setMessageCount(String messageCount) {
        this.messageCount = messageCount;
    }

    public String getMessageCount() {
        return messageCount;
    }
}
