package com.neusoft.edu.controller;

import com.neusoft.edu.feign.MonitorService;
import com.neusoft.edu.feign.user.UserInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController implements UserInterface {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MonitorService monitorService;

    @Override
    public String getInfo() {

        String message = "user service call";
        logger.info(message);
        return monitorService.getMonitorInfo();

    }
}
