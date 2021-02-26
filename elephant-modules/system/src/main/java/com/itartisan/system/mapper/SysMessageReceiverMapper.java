package com.itartisan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itartisan.api.beans.system.domain.SysMessageReceiver;

public interface SysMessageReceiverMapper extends BaseMapper<SysMessageReceiver> {
    /**
     * 更新通知状态
     *
     * @param sysMessageReceiver
     * @return
     */
    int updateMessageStatus(SysMessageReceiver sysMessageReceiver);
}
