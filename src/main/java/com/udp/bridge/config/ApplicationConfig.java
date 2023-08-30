package com.udp.bridge.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.config")
public class ApplicationConfig {

	private String queueManager;
	private String mqChannel;
	private String hostname;
	private Integer mqPort;
	private String userName;
	private String password;
	private String requestQueue;
	private String responseQueue;
	private Integer receiveTimeOut;
	private Integer listenerPort;
	private String applicationKey;
	
	public void setQueueManager(String queueManager) {
		this.queueManager = queueManager;
	}
	
	public String getQueueManager() {
		return queueManager;
	}
	
	public void setMqChannel(String mqChannel) {
		this.mqChannel = mqChannel;
	}
	
	public String getMqChannel() {
		return mqChannel;
	}
	
	public void setHostname(String hostname) {
		this.hostname = hostname;
	}
	
	public String getHostname() {
		return hostname;
	}
	
	public void setMqPort(Integer mqPort) {
		this.mqPort = mqPort;
	}
	
	public Integer getMqPort() {
		return mqPort;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setRequestQueue(String requestQueue) {
		this.requestQueue = requestQueue;
	}
	
	public String getRequestQueue() {
		return requestQueue;
	}
	
	public void setResponseQueue(String responseQueue) {
		this.responseQueue = responseQueue;
	}
	
	public String getResponseQueue() {
		return responseQueue;
	}
	
	public void setReceiveTimeOut(Integer receiveTimeOut) {
		this.receiveTimeOut = receiveTimeOut;
	}
	
	public Integer getReceiveTimeOut() {
		return receiveTimeOut;
	}
	
	public void setListenerPort(Integer listenerPort) {
		this.listenerPort = listenerPort;
	}
	
	public Integer getListenerPort() {
		return listenerPort;
	}
	
	public void setApplicationKey(String key) {
		this.applicationKey = key;
	}
	
	public String getApplicationKey() {
		return applicationKey;
	}
}