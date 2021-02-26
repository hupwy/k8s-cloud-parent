package com.itartisan.common.security.service;

import com.google.common.base.Strings;
import com.itartisan.common.core.constant.CacheConstants;
import com.itartisan.common.core.constant.Constants;
import com.itartisan.common.core.utils.UUID;
import com.itartisan.system.beans.model.LoginUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
public class TokenService {
    @Autowired
    private RedisTemplate redisTemplate;

    private final static long EXPIRE_TIME = Constants.TOKEN_EXPIRE * 60;

    private final static String ACCESS_TOKEN = CacheConstants.LOGIN_TOKEN_KEY;

    protected static final long MILLIS_SECOND = 1000;

    /**
     * 创建令牌
     */
    public Map<String, Object> createToken(LoginUser loginUser) {
        // 生成token
        String token = UUID.fastUUID().toString();

        loginUser.setToken(token);
        loginUser.setUserid(loginUser.getSysUser().getUserId());
        loginUser.setUsername(loginUser.getSysUser().getUserName());
        refreshToken(loginUser);

        // 保存或更新用户token
        Map<String, Object> map = new HashMap<>();
        map.put("access_token", token);
        map.put("expires_in", EXPIRE_TIME);
        redisTemplate.opsForValue().set(ACCESS_TOKEN + token, loginUser, EXPIRE_TIME, TimeUnit.SECONDS);
        return map;
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser() {
        return getLoginUser(((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest());
    }

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public LoginUser getLoginUser(HttpServletRequest request) {
        // 获取请求携带的令牌
        String token = getToken(request);
        if (!Strings.isNullOrEmpty(token)) {
            String userKey = getTokenKey(token);
            ValueOperations<String, LoginUser> operation = redisTemplate.opsForValue();
            LoginUser user = operation.get(userKey);
            return user;
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(LoginUser loginUser) {
        if (Objects.nonNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken())) {
            refreshToken(loginUser);
        }
    }

    public void delLoginUser(String token) {
        if (StringUtils.isNotEmpty(token)) {
            String userKey = getTokenKey(token);
            redisTemplate.delete(userKey);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + EXPIRE_TIME * MILLIS_SECOND);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisTemplate.opsForValue().set(userKey, loginUser, EXPIRE_TIME, TimeUnit.SECONDS);
    }

    private String getTokenKey(String token) {
        return ACCESS_TOKEN + token;
    }

    /**
     * 获取请求token
     */
    private String getToken(HttpServletRequest request) {
        String token = request.getHeader(CacheConstants.HEADER);
        if (!Strings.isNullOrEmpty(token) && token.startsWith(CacheConstants.TOKEN_PREFIX)) {
            token = token.replace(CacheConstants.TOKEN_PREFIX, "");
        }
        return token;
    }
}
