package net.neophyte.streaming.storm.sample.config;

import java.io.Serializable;

/**
 * 
 * @author shuvro
 *
 */
public class JMSConfiguration implements Serializable {

	private static final long serialVersionUID = 7846887674983786903L;

	private String brokerUrl = null;
	private String userId = null;
	private String password = null;
	private long receiveTimeout = 10000;

	public String getBrokerUrl() {
		return brokerUrl;
	}

	public void setBrokerUrl(String brokerUrl) {
		this.brokerUrl = brokerUrl;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public long getReceiveTimeout() {
		return receiveTimeout;
	}

	public void setReceiveTimeout(long receiveTimeout) {
		this.receiveTimeout = receiveTimeout;
	}
}
