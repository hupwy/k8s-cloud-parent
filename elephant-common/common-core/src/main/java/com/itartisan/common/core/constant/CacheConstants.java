package com.itartisan.common.core.constant;

/**
 * 缓存的key 常量
 */
public interface CacheConstants {

    /**
     * 令牌自定义标识
     */
    String HEADER = "Authorization";

    /**
     * 令牌前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * 权限缓存前缀
     */
    String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 用户ID字段
     */
    String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    String DETAILS_USERNAME = "username";
}
