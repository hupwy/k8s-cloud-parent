package com.neusoft.edu.feign;

import com.neusoft.edu.feign.monitor.MonitorInterface;
import com.neusoft.edu.feign.user.UserInterface;
import org.springframework.cloud.openfeign.FeignClient;

//@FeignClient(name = "http://192.168.2.11:32025", url = "http://192.168.2.11:32025", fallback = UserFallback.class)
//@FeignClient(name = "http://localhost:8082", url = "http://localhost:8082", fallback = UserFallback.class)
@FeignClient(name = "k8s-srv-docker-monitor", url = "${url.local:}", fallback = DockerFallback.class)
public interface MonitorService extends MonitorInterface {

}
