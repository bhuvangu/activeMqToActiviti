package com.visionael.activemq.activiti;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.jms.Message;
import javax.jms.Session;
import javax.naming.ConfigurationException;

public class Main {
	public static void main(String[] arg) throws ConfigurationException {
		String jmsHost = "localhost:61616"; 
		String jmsUrl = "failover:tcp://" + jmsHost + "?soTimeout=10000";
		String nrmAuditTopic =  "auditLog";
		String durableClientId = "NrmTaskToActivitiTaskMapper";
		String durableSubscriptionName = "NrmTaskToActivitiTaskMapperSubscriptionName";
		
		JmsProvider jmsProvider = new JmsProviderImpl(jmsUrl, nrmAuditTopic, durableClientId, durableSubscriptionName);        
        ActiveMqMsgHandler handler = new TaskMsgsHandler(jmsProvider,"com.visionael.project.entity.Task");       
        handler.start();        
	}
}
