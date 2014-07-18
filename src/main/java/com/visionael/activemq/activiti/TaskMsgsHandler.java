package com.visionael.activemq.activiti;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.naming.ConfigurationException;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.TaskService;
import org.apache.log4j.Logger;

import com.visionael.api.GuestApiFactory;
import com.visionael.api.project.ProjectApiRemote;
import com.visionael.api.project.dto.DetachedTask;
import com.visionael.api.project.dto.TaskStatus;
import com.visionael.api.vnd.query.FindResult;
import com.visionael.api.vnd.query.Query;


public class TaskMsgsHandler extends ActiveMqMsgHandler{
	private static Logger log = Logger.getLogger(TaskMsgsHandler.class);
	private TaskService avtivitiTaskService;
	private ProjectApiRemote nrmProjectApi;

	public TaskMsgsHandler(JmsProvider jmsProvider, String type) throws ConfigurationException{
		super(jmsProvider, type);
		// init activiti api
		ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();		
		avtivitiTaskService = processEngine.getTaskService();
		// init nrm api
		GuestApiFactory apiFactory = new GuestApiFactory("localhost", 3700);
		nrmProjectApi = apiFactory.getProjectApi();
	}
	
	@Override
	protected void handleTaskMsgs(Message taskMsg) throws JMSException {
		String msgChangeType = (String) taskMsg.getStringProperty(HeaderConstants.HDR_CHANGE_TYPE);
		System.out.println(taskMsg);
		if(ChangeType.DELETE.name().equals(msgChangeType)){
			// cant handle delete since entity has been deleted already from db.
			// in our case user is not allowed to delete task from nrm.
			return ;
		}
		Long taskId = Long.valueOf(taskMsg.getStringProperty(HeaderConstants.HDR_ENTITY_ID));
		DetachedTask nrmTask = getNrmDetachedTask(taskId);
		if(nrmTask == null){
			return ;
		}
		TaskStatus nrmTaskStatus = nrmTask.getStatus();
		if(TaskStatus.FINISHED.equals(nrmTaskStatus)){
			avtivitiTaskService.complete(nrmTask.getTaskId());
		}
	}

	private DetachedTask getNrmDetachedTask(Long taskId) {
		Query q = Query.find(DetachedTask.class).matching("id", taskId);		
		FindResult result = nrmProjectApi.find(q);
		if(result != null){
			return (DetachedTask) result.getFirst();
		}
		return null;
	}
}
