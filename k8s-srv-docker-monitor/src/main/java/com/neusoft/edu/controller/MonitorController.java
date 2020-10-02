package com.neusoft.edu.controller;

import com.neusoft.edu.feign.monitor.MonitorInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MonitorController implements MonitorInterface {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public String getMonitorInfo() {
        String message = "docker monitor service call";
        message += "</br> 链路调用已完成 ！";
        logger.info(message);
        return message;
    }
}
