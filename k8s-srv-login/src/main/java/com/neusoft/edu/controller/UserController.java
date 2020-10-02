package com.neusoft.edu.controller;

import com.neusoft.edu.feign.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserService userService;

    @GetMapping("/userInfo")
    public String getUserInfo() {

        String message = "user login service call";
        logger.info(message);

        return userService.getInfo();
    }
}
