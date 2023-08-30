package com.udp.bridge.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.udp.bridge.utils.ApplicationUtils;


public class SocketReceiver implements Runnable {
    private BlockingQueue<byte[]> socketToMQQueue;
    private Socket cliSock;
    private SocketSender peer;
    
    Logger log = LogManager.getLogger(SocketReceiver.class);
    
    public SocketReceiver(BlockingQueue<byte[]> socketToMQQueue, Socket cliSock, SocketSender peer) {
        this.socketToMQQueue = socketToMQQueue;
        this.cliSock = cliSock;
        this.peer = peer;
    }

    private byte[] stripHeader(byte[] message) {
    	// TODO: Add any logic here for removing header from message
    	return message;
    }
    
    @Override
    public void run() {
    	
    	log.debug("Starting Socket receiver on " + cliSock.toString() );
    	
    	try {
    		DataInputStream dis =  new DataInputStream(new BufferedInputStream(cliSock.getInputStream()));
    		byte[] message = new byte[512];
    		while (dis.read(message) > -1) {
    			String inMessage = ApplicationUtils.byteArrayToString(message);
    			log.debug("Received " + inMessage.length() + " bytes of request [" + inMessage + "]");
    			byte[] outMsg = stripHeader(inMessage.getBytes());
    			socketToMQQueue.add(outMsg);
    		}
    	} catch (IOException e) {
    		log.error("IOException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (IllegalStateException  e) {
    		log.error("IllegalStateException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (NullPointerException e) {
    		log.error("NullPointerException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (Exception e) {
    		log.error("Exception: " + e.getMessage());
    		e.printStackTrace();
    	}
    	
    	peer.setShouldTerminate(true);
    	log.debug("Exiting Socket Receiver");
    }
}
