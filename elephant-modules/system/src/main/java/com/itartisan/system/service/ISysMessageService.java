package com.itartisan.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itartisan.system.beans.domain.SysMessage;

import java.util.List;

public interface ISysMessageService extends IService<SysMessage> {

    /**
     * 分页查询
     *
     * @param page
     * @param senderId
     * @return
     */
    IPage<SysMessage> selectSysMessageByPage(IPage<SysMessage> page, Long senderId);

    /**
     * 获取某个用户的未读通知
     *
     * @param receiverId
     * @return
     */
    List<SysMessage> unread(Long receiverId);

    /**
     * 向某些用户发布消息
     *
     * @param userIds
     * @param sysMessage
     */
    void publishUser(Long[] userIds, SysMessage sysMessage);

    /**
     * 向某个机构下所有用户发布消息
     *
     * @param deptId
     * @param sysMessage
     */
    void publishDept(Long deptId, SysMessage sysMessage);

    /**
     * 删除消息
     *
     * @param msgIds
     */
    void deleteMessages(List<Long> msgIds);
}
