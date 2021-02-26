package com.itartisan.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itartisan.system.beans.domain.SysMessage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMessageMapper extends BaseMapper<SysMessage> {
    /**
     * 分页查询
     *
     * @param page
     * @param senderId
     * @return
     */
    IPage<SysMessage> selectSysMessageByPage(IPage<SysMessage> page, @Param("senderId") Long senderId);

    /**
     * 获取某个接收者的消息
     *
     * @param receiverId
     * @param status
     * @return
     */
    List<SysMessage> selectSysMessageByReceiverId(@Param("receiverId") Long receiverId, @Param("status") String status);
}
