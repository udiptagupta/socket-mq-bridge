package com.udp.bridge.mq;

import java.util.concurrent.BlockingQueue;
import com.udp.bridge.config.ApplicationConfig;


public class MQReceiver implements Runnable {
    protected BlockingQueue<byte[]> MQToSocketQueue;
    ApplicationConfig appConfig;
    
    public MQReceiver(BlockingQueue<byte[]> MQToSocketQueue, ApplicationConfig appConfig ) {
        this.MQToSocketQueue = MQToSocketQueue;
        this.appConfig = appConfig;
    }
   
	@Override
    public void run() {
        // Implement the logic to receive messages from the MQ interface
        // and add them to the MQToSocketQueue.
    }
}
