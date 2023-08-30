package com.udp.bridge.mq;

import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.udp.bridge.config.ApplicationConfig;
import com.udp.bridge.utils.ApplicationUtils;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQPutMessageOptions;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.MQConstants;


public class IbmMQSender extends MQSender {

	Logger log = LogManager.getLogger(IbmMQSender.class);
	
	public IbmMQSender(BlockingQueue<byte[]> socketToMQQueue, ApplicationConfig appConfig) {
		super(socketToMQQueue, appConfig);
	}
	
	private boolean isAuthRequired() {
		if((null != appConfig.getUserName() || appConfig.getUserName().length() > 0) 
				&& (null != appConfig.getPassword() || appConfig.getPassword().length() > 0)) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	public void run() {
		
		log.debug("Starting " + this.getName());
		MQQueue queue = null;
		MQQueueManager qMgr = null;
		
		try {
			MQEnvironment.hostname = appConfig.getHostname(); 
			MQEnvironment.port = appConfig.getMqPort().intValue();
			MQEnvironment.channel = appConfig.getMqChannel();
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);
			
			qMgr = new MQQueueManager(appConfig.getQueueManager());
			if(isAuthRequired()) {
				MQEnvironment.userID = appConfig.getUserName(); // User's username
				MQEnvironment.password = appConfig.getPassword(); // User's password
			}
			
			int openOptions = MQC.MQOO_OUTPUT | MQC.MQOO_FAIL_IF_QUIESCING;
            queue = qMgr.accessQueue(appConfig.getRequestQueue(), openOptions);
            
            log.debug(getName() + "| Established MQ connection...");
            
            MQPutMessageOptions putOptions = new MQPutMessageOptions();
            
            while (true) {
            	
            	MQMessage mqMessage = new MQMessage();
                
                // Set various MQ header fields
                mqMessage.format = MQConstants.MQFMT_STRING;
                mqMessage.replyToQueueName = appConfig.getResponseQueue();
                mqMessage.replyToQueueManagerName = appConfig.getQueueManager();
                mqMessage.correlationId = ApplicationUtils.getCorrelationID(appConfig.getApplicationKey());
                mqMessage.expiry = 5000; // Message expiration time (in ms)

                byte[] message = socketToMQQueue.take();
            	String strMsg = new String(message);
            	mqMessage.writeString(strMsg);
                
                queue.put(mqMessage, putOptions);
                log.debug(getName() + " | Send " + strMsg.length() + " bytes to " + appConfig.getResponseQueue() + ": [" + strMsg + "]");
            }
			
		} catch (MQException e) {
			log.error(getName() + "| MQException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error(getName() + "| Exception: " + e.getMessage());
			e.printStackTrace();
		} finally {
			try {
				queue.close();
				qMgr.disconnect();
			} catch (MQException e) {
				log.error(getName() + "| MQException: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
