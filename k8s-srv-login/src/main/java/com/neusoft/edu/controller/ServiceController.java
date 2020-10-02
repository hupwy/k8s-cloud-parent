package com.neusoft.edu.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ServiceController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private DiscoveryClient discoveryClient;

    @GetMapping("/service")
    public List<String> getServiceList() {
        String message = "services list";
        logger.info(message);
        return discoveryClient.getServices();
    }

    @GetMapping("/instance")
    public Object getInstance(@RequestParam("name") String name) {
        String message = "instance list";
        logger.info(message);
        return discoveryClient.getInstances(name);
    }
}
