package com.neusoft.edu.feign;

import com.neusoft.edu.feign.monitor.MonitorInterface;
import org.springframework.stereotype.Component;

@Component
public class DockerFallback implements MonitorInterface {

    @Override
    public String getMonitorInfo() {
        return "docker monitor service fallback!";
    }
}
