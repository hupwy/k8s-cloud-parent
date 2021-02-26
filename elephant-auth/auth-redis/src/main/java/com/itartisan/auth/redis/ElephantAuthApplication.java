package com.itartisan.auth.redis;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 认证授权中心
 */
@EnableFeignClients(basePackages = "com.itartisan.system.api")
@SpringCloudApplication
public class ElephantAuthApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(ElephantAuthApplication.class, args);
        System.out.println("认证授权中心启动成功!!");
    }
}
