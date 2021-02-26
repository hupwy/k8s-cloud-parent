package com.itartisan.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.core.domain.R;
import com.itartisan.common.core.web.PageSolver;
import com.itartisan.common.core.web.controller.BaseController;
import com.itartisan.common.security.annotation.PreAuthorize;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.beans.domain.SysMessage;
import com.itartisan.system.beans.domain.SysMessageReceiver;
import com.itartisan.system.service.ISysMessageReceiverService;
import com.itartisan.system.service.ISysMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.Arrays;

@Api(value = "/message", description = "站内信")
@RestController
@RequestMapping("/message")
public class SysMessageController extends BaseController {
    @Autowired
    private ISysMessageService sysMessageService;
    @Autowired
    private ISysMessageReceiverService sysMessageReceiverService;

    @ApiOperation("获取通知列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页", dataTypeClass = Integer.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "pageSize", value = "单页容量", dataTypeClass = Integer.class, paramType = "query", required = true),
            @ApiImplicitParam(name = "senderId", value = "发送者id", dataTypeClass = Long.class, paramType = "query")
    })
    @PreAuthorize(hasPermi = "system:message:list")
    @GetMapping("/list")
    public IPage<SysMessage> list(@ApiIgnore @PageSolver Page<SysMessage> page,
                                  @RequestParam(value = "senderId", required = false) Long senderId) {
        return sysMessageService.selectSysMessageByPage(page, senderId);
    }

    @ApiOperation("获取当前用户未读通知列表")
    @PreAuthorize(hasPermi = "system:message:query")
    @GetMapping("/unread")
    public AjaxResult unread() {
        return AjaxResult.success(sysMessageService.unread(SecurityUtils.getUserId()));
    }

    @ApiOperation("手动发布站内信")
    @PreAuthorize(hasPermi = "system:message:add")
    @PostMapping
    public AjaxResult add(@Validated @RequestBody SysMessage sysMessage) {
        sysMessage.setSenderId(SecurityUtils.getUserId());
        sysMessage.setCreateBy(SecurityUtils.getUsername());
        Long[] receiverIds = sysMessage.getReceiverIds();
        if (receiverIds != null && receiverIds.length > 0) {
            sysMessageService.publishUser(receiverIds, sysMessage);
        }
        return AjaxResult.success();
    }

    @ApiOperation("标记通知状态")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "msgId", value = "通知id", dataTypeClass = Long.class, paramType = "path"),
            @ApiImplicitParam(name = "status", value = "要标记的通知状态（0未读,1已读）", allowableValues = "0,1", dataTypeClass = String.class, paramType = "path"),
    })
    @PreAuthorize(hasPermi = "system:message:edit")
    @PutMapping("/{msgId}/status/{status}")
    public AjaxResult changeMessageStatus(@PathVariable("msgId") Long msgId, @PathVariable("status") String status) {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<SysMessageReceiver> queryWrapper = Wrappers.lambdaQuery(SysMessageReceiver.class)
                .eq(SysMessageReceiver::getMsgId, msgId)
                .eq(SysMessageReceiver::getReceiverId, userId);

        SysMessageReceiver messageReceiver = sysMessageReceiverService.getOne(queryWrapper);
        if (messageReceiver != null) {
            messageReceiver.setUpdateBy(SecurityUtils.getUsername());
            messageReceiver.setStatus(status);
            return toAjax(sysMessageReceiverService.changeMessageStatus(messageReceiver));
        } else {
            return AjaxResult.error("未找到此通知！");
        }
    }

    @ApiOperation("删除通知")
    @PreAuthorize(hasPermi = "system:message:remove")
    @DeleteMapping("/{msgIds}")
    public AjaxResult remove(@PathVariable Long[] msgIds) {
        sysMessageService.deleteMessages(Arrays.asList(msgIds));
        return AjaxResult.success();
    }

    /**
     * 向某个机构下所有用户发布消息
     *
     * @param deptId
     * @param sysMessage
     * @return
     */
    @ApiIgnore
    @PostMapping("/publish/dept/{deptId}")
    public R<SysMessage> publishDept(@PathVariable("deptId") Long deptId, SysMessage sysMessage) {
        sysMessageService.publishDept(deptId, sysMessage);
        return R.ok();
    }

    /**
     * 向某些用户发布消息
     *
     * @param userIds
     * @param sysMessage
     * @return
     */
    @ApiIgnore
    @PostMapping("/publish/user/{userIds}")
    public R<SysMessage> publishUser(@PathVariable("userIds") Long[] userIds, SysMessage sysMessage) {
        sysMessageService.publishUser(userIds, sysMessage);
        return R.ok();
    }
}
