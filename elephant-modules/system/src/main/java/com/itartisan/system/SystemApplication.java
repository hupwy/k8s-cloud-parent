package com.itartisan.system;


import com.itartisan.common.swagger.annotation.EnableCustomSwagger3;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 系统服务
 */
@EnableCustomSwagger3
@MapperScan("com.neuedu.cloudlab.system.mapper")
@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true)
public class SystemApplication {

    @Bean
    public ConfigurableServletWebServerFactory webServerFactory() {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        factory.addConnectorCustomizers(connector -> connector.setProperty("relaxedQueryChars", "|{}[]"));
        return factory;
    }

    public static void main(String[] args) {
        SpringApplication.run(SystemApplication.class, args);
    }
}
