package com.visionael.activemq.activiti;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;

import com.sun.enterprise.Switch;
import com.visionael.api.project.dto.DetachedTask;
import com.visionael.api.project.dto.TaskStatus;

public abstract class ActiveMqMsgHandler {
	public enum ChangeType {
        INSERT, UPDATE, DELETE, COLLECTION_RECREATE, COLLECTION_REMOVE, COLLECTION_UPDATE, GET_ENTITY, STATEMENT, RELATED_ENTITY_CHANGE
    }
	private String FILTER_ENTITY_TYPE = "";
	private static Logger log = Logger.getLogger(ActiveMqMsgHandler.class);
	private ActiveMqDurableSession activeMqDurableSession = null;

	public ActiveMqMsgHandler(JmsProvider jmsProvider, String entityFilterType) {
		this.FILTER_ENTITY_TYPE = entityFilterType;
		this.activeMqDurableSession = jmsProvider.getNrmAuditSession();
		while (activeMqDurableSession == null) {
			log.error("Cant connect to activeMq, will try again, after 2 sec");
			sleep();
		}		
		this.activeMqDurableSession.setAcknowledge(Session.AUTO_ACKNOWLEDGE);
		this.activeMqDurableSession.start();
	}

	public void start() {
		while (true) {
			Message m = this.activeMqDurableSession.readMessage(0);
			if(m != null){
				try{
					String entityType = m.getStringProperty(HeaderConstants.HDR_ENTITY_TYPE);
					if(FILTER_ENTITY_TYPE.equals(entityType)){
						handleTaskMsgs(m);
					}
				}catch(Exception e){
					log.error("error reading msg ", e);
				}
			}
			
		}
	}

	protected abstract void handleTaskMsgs(Message m) throws JMSException;

	private void sleep() {
		try {
			Thread.sleep(2000L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
