package com.udp.bridge.net;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

import com.udp.bridge.utils.ApplicationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SocketReceiver extends Thread {
    private BlockingQueue<byte[]> socketToMQQueue;
    private Socket cliSock;
    private Thread peer;
    
    public SocketReceiver(BlockingQueue<byte[]> socketToMQQueue, Socket cliSock, Thread peer) {
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
    	
    	log.debug("Starting " + this.getName());
    	
    	try {
    		DataInputStream dis =  new DataInputStream(new BufferedInputStream(cliSock.getInputStream()));
    		byte[] message = new byte[512];
    		while (dis.read(message) > -1) {
    			String inMessage = ApplicationUtils.byteArrayToString(message);
    			log.debug(getName() + "| Received " + inMessage.length() + " bytes of request [" + inMessage + "]");
    			byte[] outMsg = stripHeader(inMessage.getBytes());
    			socketToMQQueue.add(outMsg);
    		}
    	} catch (IOException e) {
    		log.error(getName() + "| IOException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (IllegalStateException  e) {
    		log.error(getName() + "| IllegalStateException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (NullPointerException e) {
    		log.error(getName() + "| NullPointerException: " + e.getMessage());
    		e.printStackTrace();
    	} catch (Exception e) {
    		log.error(getName() + "| Exception: " + e.getMessage());
    		e.printStackTrace();
    	}
    	peer.interrupt();
    }
}
