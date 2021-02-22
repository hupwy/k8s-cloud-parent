package com.itartisan.common.security.utils;

import com.itartisan.common.core.constant.CacheConstants;
import com.itartisan.common.core.text.Convert;
import com.itartisan.common.core.utils.ServletUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 权限获取工具类
 */
public class SecurityUtils {
    /**
     * 获取用户
     */
    public static String getUsername() {
        return ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_USERNAME);
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        return Convert.toLong(ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_USER_ID));
    }

    /**
     * 获取用户昵称
     */
    public static String getUserNickName() {
        try {
            return URLDecoder.decode(ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_USERNICKNAME), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 获取用户所属部门id
     *
     * @return
     */
    public static Long getDeptId() {
        return Convert.toLong(ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_DEPT_ID));
    }

    /**
     * 获取用户所属部门名称
     *
     * @return
     */
    public static String getDeptName() {
        try {
            return URLDecoder.decode(ServletUtils.getRequest().getHeader(CacheConstants.DETAILS_DEPTNAME), StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 是否为管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isAdmin(Long userId) {
        return userId != null && 1L == userId;
    }

    /**
     * 生成BCryptPasswordEncoder密码
     *
     * @param password 密码
     * @return 加密字符串
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否相同
     *
     * @param rawPassword     真实密码
     * @param encodedPassword 加密后字符
     * @return 结果
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
