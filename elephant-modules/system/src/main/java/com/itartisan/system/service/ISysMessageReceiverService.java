package com.itartisan.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itartisan.system.beans.domain.SysMessageReceiver;

public interface ISysMessageReceiverService extends IService<SysMessageReceiver> {
    /**
     * 更改消息状态
     *
     * @param sysMessageReceiver
     * @return
     */
    int changeMessageStatus(SysMessageReceiver sysMessageReceiver);
}
