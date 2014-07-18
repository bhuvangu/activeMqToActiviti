package com.visionael.activemq.activiti;
import javax.jms.MessageProducer;

public interface JmsProvider {
    ActiveMqDurableSession getNrmAuditSession();
}