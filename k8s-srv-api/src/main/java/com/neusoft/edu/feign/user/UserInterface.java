package com.neusoft.edu.feign.user;

import org.springframework.web.bind.annotation.GetMapping;

public interface UserInterface {

    /**
     * 获取用户测试信息
     * @return
     */
    @GetMapping()
    public String getInfo();

}
