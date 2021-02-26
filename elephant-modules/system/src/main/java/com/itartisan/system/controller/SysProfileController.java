package com.itartisan.system.controller;

import com.itartisan.common.core.domain.AjaxResult;
import com.itartisan.common.security.service.TokenService;
import com.itartisan.common.security.utils.SecurityUtils;
import com.itartisan.system.beans.domain.SysUser;
import com.itartisan.system.beans.model.LoginUser;
import com.itartisan.system.service.ISysUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 个人信息 业务处理
 */
@Api(value = "/user", description = "个人信息")
@RestController
@RequestMapping("/user/profile")
public class SysProfileController {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private TokenService tokenService;

    /**
     * 修改用户
     */
    @ApiOperation("修改用户个人信息")
    @PutMapping
    public AjaxResult updateProfile(@RequestBody SysUser user) {
        Long userId = SecurityUtils.getUserId();
        user.setUserId(userId);
        if (userService.updateUserProfile(user) > 0) {
            LoginUser loginUser = tokenService.getLoginUser();
            // 更新缓存用户信息
            loginUser.getSysUser().setNickName(user.getNickName());
            loginUser.getSysUser().setEmail(user.getEmail());
            tokenService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error("修改个人信息异常，请联系管理员");
    }

    /**
     * 重置密码
     */
    @PutMapping("/updatePwd")
    public AjaxResult updatePwd(@RequestBody Map<String, String> body) {
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        String username = SecurityUtils.getUsername();
        SysUser user = userService.selectUserByUserName(username);
        String password = user.getPassword();
        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return AjaxResult.error("修改密码失败，旧密码错误");
        }
        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return AjaxResult.error("新密码不能与旧密码相同");
        }
        if (userService.resetUserPwd(username, SecurityUtils.encryptPassword(newPassword)) > 0) {
            // 更新缓存用户密码
            LoginUser loginUser = tokenService.getLoginUser();
            loginUser.getSysUser().setPassword(SecurityUtils.encryptPassword(newPassword));
            tokenService.setLoginUser(loginUser);
            return AjaxResult.success();
        }
        return AjaxResult.error("修改密码异常，请联系管理员");
    }
}
