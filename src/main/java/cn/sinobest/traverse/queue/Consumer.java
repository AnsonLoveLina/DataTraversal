package cn.sinobest.traverse.queue;

import org.springframework.jms.core.JmsTemplate;

/**
 * Created by zy-xx on 16/2/2.
 */
public class Consumer {
    private JmsTemplate template;
    private String destination;

    public void setTemplate(JmsTemplate template) {
        this.template = template;
    }

    public JmsTemplate getTemplate() {
        return template;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }
}
