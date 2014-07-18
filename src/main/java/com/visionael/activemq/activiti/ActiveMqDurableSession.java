package com.visionael.activemq.activiti;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.Topic;
import javax.jms.TopicConnection;
import javax.jms.TopicSession;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

public class ActiveMqDurableSession {

    private static final Logger log = Logger.getLogger(ActiveMqDurableSession.class);
    private final String brokerUrl;
    private final String topicName;
    private TopicConnection topicConnection;
    private TopicSession topicSession;
    private Topic topic;
    private TopicSubscriber topicSubscriber;
    private int autoAcknowledge = Session.AUTO_ACKNOWLEDGE;
    private final String clientId;
    private final String subscriptionName;

    public ActiveMqDurableSession(
            String brokerUrl,
            String topicName,
            String clientId,
            String subscriptionName) {
        this.brokerUrl = brokerUrl;
        this.topicName = topicName;
        this.clientId = clientId;
        this.subscriptionName = subscriptionName;
    }

    public void start() {
        try {
            ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(brokerUrl);
            factory.setAlwaysSessionAsync(false);
            topicConnection = factory.createTopicConnection();
            topicConnection.setClientID(clientId);
            topicSession = topicConnection.createTopicSession(false, autoAcknowledge);
            topic = topicSession.createTopic(topicName);
            //            topicSubscriber = topicSession.createDurableSubscriber(topic, subscriptionName);
            topicSubscriber = topicSession.createSubscriber(topic); // RSM: UNTIL NEW VERSION OF ACTIVEMQ, NO DURABLE SUBSCRIPTION!
            topicConnection.start();
        } catch (Throwable r) {
            log.fatal("start", r);
        }
    }

    public void setAcknowledge(int ack) {
        autoAcknowledge = ack;
    }

    public void close() {
        try {
            topicSession.close();
        } catch (JMSException e) {
        }
        try {
            topicConnection.stop();
        } catch (JMSException e) {
        }
        try {
            topicConnection.close();
        } catch (JMSException e) {
        }
    }

    public Message readMessage(long timeout) {
        try {

            return topicSubscriber.receive(timeout);
        } catch (JMSException e) {
            Throwable original = e.getCause();
            if (!(original instanceof InterruptedException)) {
                log.error("readMessage", e);
            }
            return null;
        }
    }
}
