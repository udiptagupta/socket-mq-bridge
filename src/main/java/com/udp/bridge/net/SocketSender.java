package com.udp.bridge.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.udp.bridge.utils.ApplicationUtils;


public class SocketSender implements Runnable {
    private BlockingQueue<byte[]> MQToSocketQueue;
    private Socket cliSock;
    private boolean shouldTerminate;

    Logger log = LogManager.getLogger(SocketSender.class);

    public void setShouldTerminate(boolean terminated) {
    	this.shouldTerminate = terminated;
    }
    
    public boolean isShouldTerminate() {
    	return shouldTerminate;
    }

    public SocketSender(BlockingQueue<byte[]> MQToSocketQueue, Socket cliSock) {
        this.MQToSocketQueue = MQToSocketQueue;
        this.cliSock = cliSock;
        this.shouldTerminate = false;
    }

	private byte[] addHeader(byte[] inMsg) {
		// TODO: Add any logic to add header to the existing message
    	return inMsg;
    }
    
    @Override
    public void run() {
    	
    	log.debug("Starting Socket Sender on " + cliSock.toString());
    	
    	try {
    		DataOutputStream dos = new DataOutputStream(this.cliSock.getOutputStream());
		
	        while (!shouldTerminate) {
            	if(cliSock.isConnected()) {
            		// Dequeue a message from MQToSocketQueue and send it back to the socket interface.
            		byte[] message = MQToSocketQueue.poll(500, TimeUnit.MILLISECONDS);
	            		if(null != message) {
	            		String outString = ApplicationUtils.byteArrayToString(message);
	            		byte[] outMsg = addHeader(outString.getBytes());
	            		dos.write(outMsg);
	            		
	            		log.debug("Sent " + outString.length() + "  bytes of response [" + outString + "]");
            		}
            	}
	        }
    	} catch (InterruptedException e) {
    		log.error("InterruptedException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (IOException e) {
    		log.error("IOException: " + e.getMessage());
	        e.printStackTrace();
    	} catch (Exception e) {
    		log.error("Exception: " + e.getMessage());
    		e.printStackTrace();
    	}
    	
    	log.debug("Exiting Socket Sender");
    }
}