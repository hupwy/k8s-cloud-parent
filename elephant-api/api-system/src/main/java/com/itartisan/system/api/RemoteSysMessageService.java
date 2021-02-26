package com.itartisan.system.api;

import com.itartisan.system.beans.domain.SysMessage;
import com.itartisan.common.core.domain.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * 站内信服务
 */
@FeignClient(contextId = "remoteSysMessageService", value = "system", url = "${local.feign.server.system.url:}")
public interface RemoteSysMessageService {
    /**
     * 向某些用户发布消息
     *
     * @param userIds
     * @param sysMessage
     * @return
     */
    @PostMapping("/message/publish/user/{userIds}")
    R<SysMessage> publishUser(@PathVariable("userIds") Long[] userIds, SysMessage sysMessage);
}
