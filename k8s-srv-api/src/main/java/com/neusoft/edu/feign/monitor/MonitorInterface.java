package com.neusoft.edu.feign.monitor;

import org.springframework.web.bind.annotation.GetMapping;

public interface MonitorInterface {

    /**
     * 获取Docker监控测试信息
     * @return
     */
    @GetMapping()
    public String getMonitorInfo();
}
