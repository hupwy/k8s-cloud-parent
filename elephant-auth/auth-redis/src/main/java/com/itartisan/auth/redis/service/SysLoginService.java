package com.itartisan.auth.redis.service;

import com.itartisan.system.api.RemoteSysUserService;
import com.itartisan.system.api.beans.domain.SysUser;
import com.itartisan.system.api.beans.model.LoginUser;
import com.itartisan.common.core.constant.UserConstants;
import com.itartisan.common.core.domain.R;
import com.itartisan.common.core.enums.UserStatus;
import com.itartisan.common.core.exception.BaseException;
import io.fabric8.zjsonpatch.internal.guava.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 登录校验方法
 */
@Service
public class SysLoginService {

    @Autowired
    private RemoteSysUserService remoteUserService;

    /**
     * 登录
     */
    public LoginUser login(String username, String password) {
        // 用户名或密码为空 错误
        if (Strings.isNullOrEmpty(username) || Strings.isNullOrEmpty(password)) {
            throw new BaseException("用户/密码必须填写");
        }
        // 密码如果不在指定范围内 错误
        if (password.length() < UserConstants.PASSWORD_MIN_LENGTH
                || password.length() > UserConstants.PASSWORD_MAX_LENGTH) {
            throw new BaseException("用户密码不在指定范围");
        }
        // 用户名不在指定范围内 错误
        if (username.length() < UserConstants.USERNAME_MIN_LENGTH
                || username.length() > UserConstants.USERNAME_MAX_LENGTH) {
            throw new BaseException("用户名不在指定范围");
        }
        // 查询用户信息
        R<LoginUser> userResult = remoteUserService.getUserInfo(username);
        if (R.FAIL == userResult.getCode()) {
            throw new BaseException(userResult.getMsg());
        }
        LoginUser userInfo = userResult.getData();
        SysUser user = userInfo.getSysUser();
        if (user == null) {
            throw new BaseException("登录用户：" + username + " 不存在");
        }

        if (UserStatus.DELETED.getCode().equals(user.getDelFlag())) {
            throw new BaseException("对不起，您的账号：" + username + " 已被删除");
        }
        if (UserStatus.DISABLE.getCode().equals(user.getStatus())) {
            throw new BaseException("对不起，您的账号：" + username + " 已停用");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        passwordEncoder.matches(password, user.getPassword());
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BaseException("用户不存在/密码错误");
        }
        return userInfo;
    }
}
