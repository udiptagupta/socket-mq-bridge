package com.udp.bridge.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.udp.bridge.config.ApplicationConfig;
import com.udp.bridge.mq.*;
import com.udp.bridge.net.SocketReceiver;
import com.udp.bridge.net.SocketSender;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BridgeService {

	@Autowired
	ApplicationConfig appConfig;
	
	private ServerSocket svrSocket;
	private BlockingQueue<byte[]> socketToMQQueue;
	private BlockingQueue<byte[]> MQToSocketQueue;
	
	public void bridgeServer() {
		Socket cliSocket = null;
		// Shared blocking queues to hold messages
		socketToMQQueue = new LinkedBlockingQueue<>();
		MQToSocketQueue = new LinkedBlockingQueue<>();
		
		int port = appConfig.getListenerPort().intValue();
		try {
			svrSocket = new ServerSocket(port, 200);
			svrSocket.setReuseAddress(true);
			
			MQReceiver mqReceiver = new IbmMQReceiver(MQToSocketQueue, appConfig);
			MQSender mqSender = new IbmMQSender(socketToMQQueue, appConfig);

			mqReceiver.start();
			mqSender.start();
			
			while(true) {
				log.debug("Waiting for client on port " + svrSocket.getLocalPort());
				cliSocket = svrSocket.accept();
				
				log.debug("Received connection from " + cliSocket.getInetAddress().getHostAddress());
				// hand over connection to client handler thread
				
				SocketReceiver socketReceiver = new SocketReceiver(socketToMQQueue, cliSocket);
				SocketSender socketSender = new SocketSender(MQToSocketQueue, cliSocket);

				socketReceiver.start();
				socketSender.start();
			}
		} catch (SocketException e) {
			log.error("SocketException: "+ e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			log.error("IOException: "+ e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			log.error("Exception: "+ e.getMessage());
			e.printStackTrace();
		}		
	}

}
