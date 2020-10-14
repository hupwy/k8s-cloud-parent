package com.itartisan.system.api;

import com.itartisan.beans.model.LoginUser;
import com.itartisan.common.core.constant.ServiceNameConstants;
import com.itartisan.common.core.domain.R;
import com.itartisan.system.api.factory.RemoteUserFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 用户服务
 */
@FeignClient(contextId = "remoteUserService", value = ServiceNameConstants.SYSTEM_SERVICE, fallbackFactory = RemoteUserFallbackFactory.class)
public interface RemoteUserService {
    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 结果
     */
    @GetMapping(value = "/user/info/{username}")
    R<LoginUser> getUserInfo(@PathVariable("username") String username);
}
