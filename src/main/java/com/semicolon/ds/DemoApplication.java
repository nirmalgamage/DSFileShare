package com.semicolon.ds;

import com.semicolon.ds.controller.NodeController;
import com.semicolon.ds.core.GnutellaNode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.UUID;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

	}


	@Bean
	public GnutellaNode gnutellaNode(ApplicationContext ctx) throws Exception {
		String uniqueID = UUID.randomUUID().toString();
		GnutellaNode node = new GnutellaNode("node" + uniqueID);
		node.init();
		return node;
	}
}
