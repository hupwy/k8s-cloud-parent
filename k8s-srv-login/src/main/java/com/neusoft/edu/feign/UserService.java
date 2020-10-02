package com.neusoft.edu.feign;

import com.neusoft.edu.feign.user.UserInterface;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "k8s-srv-user", url = "${url.local:}", fallback = UserFallback.class)
public interface UserService extends UserInterface {

}
