package com.udp.bridge.mq;

import java.util.concurrent.BlockingQueue;

import com.udp.bridge.config.ApplicationConfig;
import com.ibm.mq.MQC;
import com.ibm.mq.MQEnvironment;
import com.ibm.mq.MQException;
import com.ibm.mq.MQGetMessageOptions;
import com.ibm.mq.MQMessage;
import com.ibm.mq.MQQueue;
import com.ibm.mq.MQQueueManager;
import com.ibm.mq.constants.CMQC;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IbmMQReceiver extends MQReceiver {

	public IbmMQReceiver(BlockingQueue<byte[]> MQToSocketQueue, ApplicationConfig appConfig) {
		super(MQToSocketQueue, appConfig);
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
		
		log.debug("Starting MQ receiver " + this.getName());
		
		MQQueue queue = null;
		MQQueueManager qMgr = null;
		
		try {
			MQEnvironment.hostname = appConfig.getHostname(); 
			MQEnvironment.port = appConfig.getMqPort().intValue();
			MQEnvironment.channel = appConfig.getMqChannel();
			
			MQEnvironment.properties.put(MQC.TRANSPORT_PROPERTY, MQC.TRANSPORT_MQSERIES_CLIENT);
			if(isAuthRequired()) {
				MQEnvironment.userID = appConfig.getUserName(); // User's username
				MQEnvironment.password = appConfig.getPassword(); // User's password
			}
			
			qMgr = new MQQueueManager(appConfig.getQueueManager());

			int openOptions = MQC.MQOO_INPUT_AS_Q_DEF | MQC.MQOO_FAIL_IF_QUIESCING;
            queue = qMgr.accessQueue(appConfig.getResponseQueue(), openOptions);
            
            log.debug(getName() + "| Established MQ connection...");
            
            MQGetMessageOptions getOptions = new MQGetMessageOptions();
            getOptions.options = CMQC.MQGMO_WAIT | CMQC.MQGMO_FAIL_IF_QUIESCING;
            getOptions.waitInterval = 5000; // 2 seconds
            
            while (true) {
            	
            	try {
					MQMessage mqMessage = new MQMessage();
					queue.get(mqMessage, getOptions);
					String inMsg = mqMessage.readStringOfByteLength(mqMessage.getMessageLength());
					
					byte[] message = inMsg.getBytes();
					MQToSocketQueue.add(message);
					String correlationId = new String(mqMessage.correlationId);
					log.debug(getName() + "| CorrelationID: " + correlationId);
					log.debug(getName() + " | Received " + inMsg.length() + " bytes from " + appConfig.getResponseQueue() + ": [" + inMsg + "]");
				} catch (MQException e) {
					if ( (e.completionCode == CMQC.MQCC_FAILED) &&
					           (e.reasonCode == CMQC.MQRC_NO_MSG_AVAILABLE) )
					      {
					         // No message - loop again
					      }
					      else
					      {
					         log.error(getName() + "| MQException: " + e.getLocalizedMessage());
					         log.error(getName() + "| CC=" + e.completionCode + " : RC=" + e.reasonCode);
					         e.printStackTrace();
					      }			
				}
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
