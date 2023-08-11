package com.udp.bridge;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.udp.bridge.service.BridgeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class SocketMqBridgeApplication  implements CommandLineRunner{

	@Autowired
	private BridgeService bridgeService;
	
	public static void main(String[] args) {
		SpringApplication.run(SocketMqBridgeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Starting GDS bridge application...");
		bridgeService.bridgeServer();
	}


}
