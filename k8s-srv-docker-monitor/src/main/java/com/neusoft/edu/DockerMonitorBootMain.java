package com.neusoft.edu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class DockerMonitorBootMain {

    public static void main(String[] args) {
        SpringApplication.run(DockerMonitorBootMain.class, args);
    }

}
