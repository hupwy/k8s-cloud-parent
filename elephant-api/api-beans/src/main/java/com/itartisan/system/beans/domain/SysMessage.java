package com.itartisan.system.beans.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.itartisan.common.core.domain.BaseEntity;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 站内信表 sys_message
 */
public class SysMessage extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /**
     * 通知表ID
     */
    @TableId(type = IdType.AUTO)
    @ApiModelProperty(hidden = true)
    private Long msgId;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    @Size(max = 50, message = "通知标题不能超过50个字符")
    private String msgTitle;

    /**
     * 通知内容
     */
    @NotBlank(message = "通知内容不能为空")
    private String msgContent;

    /**
     * 发送用户ID
     */
    @ApiModelProperty(hidden = true)
    private Long senderId;

    /**
     * 发送用户昵称
     */
    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private String senderNickName;

    /**
     * 接收者id
     */
    @TableField(exist = false)
    private Long[] receiverIds;

    @TableField(exist = false)
    @ApiModelProperty(hidden = true)
    private List<SysMessageReceiver> receivers;

    public Long getMsgId() {
        return msgId;
    }

    public void setMsgId(Long msgId) {
        this.msgId = msgId;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickName() {
        return senderNickName;
    }

    public void setSenderNickName(String senderNickName) {
        this.senderNickName = senderNickName;
    }

    public Long[] getReceiverIds() {
        return receiverIds;
    }

    public void setReceiverIds(Long[] receiverIds) {
        this.receiverIds = receiverIds;
    }

    public List<SysMessageReceiver> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<SysMessageReceiver> receivers) {
        this.receivers = receivers;
    }
}
