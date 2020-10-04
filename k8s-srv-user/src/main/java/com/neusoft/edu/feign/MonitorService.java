package com.neusoft.edu.feign;

import com.neusoft.edu.feign.monitor.MonitorInterface;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "k8s-srv-docker-monitor", url = "${url.local:}", fallback = DockerFallback.class)
public interface MonitorService extends MonitorInterface {

}
