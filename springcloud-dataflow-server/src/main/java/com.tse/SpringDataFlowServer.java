package com.tse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.dataflow.server.EnableDataFlowServer;

@SpringBootApplication
@EnableDataFlowServer
public class SpringDataFlowServer {
    public static void main(String[] args) {
        SpringApplication.run(SpringDataFlowServer.class, args);
    }
}
