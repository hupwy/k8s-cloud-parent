package com.neusoft.edu.feign;

import org.springframework.stereotype.Component;

@Component
public class UserFallback implements UserService {

    @Override
    public String getInfo() {
        return "user service fallback!";
    }

}
