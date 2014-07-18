package com.visionael.activemq.activiti;

public class JmsProviderImpl implements JmsProvider {

	private String jmsUrl;
	private String nrmAuditTopic;
	private String durableClientId;
	private String durableSubscriptionName;

	public JmsProviderImpl(String jmsUrl, String nrmAuditTopic,
			String durableClientId, String durableSubscriptionName) {
		this.jmsUrl = jmsUrl;
		this.nrmAuditTopic = nrmAuditTopic;
		this.durableClientId = durableClientId;
		this.durableSubscriptionName = durableSubscriptionName;
	}

	@Override
	public ActiveMqDurableSession getNrmAuditSession() {
		return new ActiveMqDurableSession(jmsUrl, nrmAuditTopic,
				durableClientId, durableSubscriptionName);
	}

}