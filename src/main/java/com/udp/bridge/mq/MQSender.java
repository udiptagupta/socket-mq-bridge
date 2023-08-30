package com.udp.bridge.mq;

import java.util.concurrent.BlockingQueue;
import com.udp.bridge.config.ApplicationConfig;

public class MQSender implements Runnable {
    protected BlockingQueue<byte[]> socketToMQQueue;
    ApplicationConfig appConfig;
    
    public MQSender(BlockingQueue<byte[]> socketToMQQueue, ApplicationConfig appConfig) {
        this.socketToMQQueue = socketToMQQueue;
        this.appConfig = appConfig;
    }

    @Override
    public void run() {
    	// Dequeue a message from socketToMQQueue and send it to the MQ interface.
    	// Implement the logic to send the message to the MQ interface.
                
    }
}
