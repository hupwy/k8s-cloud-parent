package com.itartisan.system.beans.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.itartisan.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 站内信接收表 sys_message_receiver
 */
public class SysMessageReceiver extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 通知表ID
     */
    private Long msgId;

    /**
     * 接收用户ID
     */
    private Long receiverId;

    /**
     * 接收人名称
     */
    @TableField(exist = false)
    private String receiverUserName;

    /**
     * 通知状态（0未读 1已读）
     */
    private String status;

    /**
     * 已读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverUserName() {
        return receiverUserName;
    }

    public void setReceiverUserName(String receiverUserName) {
        this.receiverUserName = receiverUserName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}
