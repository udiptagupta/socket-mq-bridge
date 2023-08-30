package com.udp.bridge.net;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.udp.bridge.utils.ApplicationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketSender extends Thread {
    private BlockingQueue<byte[]> MQToSocketQueue;
    private Socket cliSock;
    
    public SocketSender(BlockingQueue<byte[]> MQToSocketQueue, Socket cliSock) {
        this.MQToSocketQueue = MQToSocketQueue;
        this.cliSock = cliSock;
    }

	private byte[] addHeader(byte[] inMsg) {
		// TODO: Add any logic to add header to the existing message
    	return inMsg;
    }
    
    @Override
    public void run() {
    	
    	log.debug("Starting " + this.getName());
    	
    	try {
    		DataOutputStream dos = new DataOutputStream(this.cliSock.getOutputStream());
		
	        while (true) {
            	if(cliSock.isConnected()) {
            		// Dequeue a message from MQToSocketQueue and send it back to the socket interface.
            		byte[] message = MQToSocketQueue.poll(500, TimeUnit.MILLISECONDS);
            		if(null != message) {
	            		String outString = ApplicationUtils.byteArrayToString(message);
	            		byte[] outMsg = addHeader(outString.getBytes());
	            		dos.write(outMsg);
	            		
	            		log.debug(getName() + "| Sent " + outString.length() + "  bytes of response [" + outString + "]");
            		}
            	}
	        }
    	} catch (InterruptedException e) {
    		log.error(getName() + "| InterruptedException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (IOException e) {
    		log.error(getName() + "| IOException: " + e.getMessage());
	        e.printStackTrace();
    	} catch (Exception e) {
    		log.error(getName() + "| Exception: " + e.getMessage());
    		e.printStackTrace();
    	}
    }
}