package com.itartisan.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.ParserConfig;
import com.itartisan.gateway.config.properties.IgnoreWhiteProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * 网关鉴权
 */
@Component
public class AuthFilter implements GlobalFilter, Ordered {
    static {
        ParserConfig.getGlobalInstance().setAutoTypeSupport(true);
    }

    private static final Logger log = LoggerFactory.getLogger(AuthFilter.class);

    private final static long EXPIRE_TIME = 720 * 60;

    // 后端服务url前缀
    @Value("${backend-service-alias}")
    private String backendServiceAlias;

    @Autowired
    private IgnoreWhiteProperties ignoreWhite;
    @Resource(name = "stringRedisTemplate")
    private ValueOperations<String, String> sops;
    @Autowired
    private RedisTemplate redisTemplate;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final String TOKEN_PREFIX = "Bearer ";
    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";
    /**
     * 用户ID字段
     */
    public static final String DETAILS_USER_ID = "user_id";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNAME = "username";

    /**
     * 用户名字段
     */
    public static final String DETAILS_USERNICKNAME = "user_nick_name";

    /**
     * 部门名称
     */
    public static final String DETAILS_DEPTNAME = "deptName";

    /**
     * 部门id
     */
    public static final String DETAILS_DEPTID = "deptId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String url = exchange.getRequest().getURI().getPath();
        // 跳过不需要验证的路径
        for (String pattern : ignoreWhite.getWhites()) {
            if (antPathMatcher.match(backendServiceAlias + pattern, url)) {
                return chain.filter(exchange);
            }
        }

        String token = getToken(exchange.getRequest());
        if (StringUtils.isEmpty(token)) {
            return setUnauthorizedResponse(exchange, "令牌不能为空");
        }

        String userStr = sops.get(getTokenKey(token));
        if (StringUtils.isEmpty(userStr)) {
            return setUnauthorizedResponse(exchange, "登录状态已过期");
        }
        JSONObject obj = JSONObject.parseObject(userStr);
        String userid = obj.getString("userid");
        String username = obj.getString("username");
        if (StringUtils.isEmpty(userid) || StringUtils.isEmpty(username)) {
            return setUnauthorizedResponse(exchange, "令牌验证失败");
        }

        JSONObject sysUser = obj.getJSONObject("sysUser");
        String userNickName = sysUser.getString("nickName");
        JSONObject dept = sysUser.getJSONObject("dept");
        String deptname = dept.getString("deptName");
        String deptid = dept.getString("deptId");
        // 设置过期时间
        redisTemplate.expire(getTokenKey(token), EXPIRE_TIME, TimeUnit.SECONDS);
        // 设置用户信息到请求
        ServerHttpRequest mutableReq = null;
        try {
            mutableReq = exchange.getRequest().mutate()
                    .header(DETAILS_USER_ID, userid)
                    .header(DETAILS_USERNAME, username)
                    .header(DETAILS_USERNICKNAME, URLEncoder.encode(StringUtils.isEmpty(userNickName) ? "" : userNickName,StandardCharsets.UTF_8.name()))
                    .header(DETAILS_DEPTNAME, URLEncoder.encode(StringUtils.isEmpty(deptname) ? "" : deptname,StandardCharsets.UTF_8.name()))
                    .header(DETAILS_DEPTID, deptid)
                    .build();
        } catch (UnsupportedEncodingException e) {
            log.error("用户所属机构中文转码异常", e);
        }
        ServerWebExchange mutableExchange = exchange.mutate().request(mutableReq).build();

        return chain.filter(mutableExchange);
    }

    @Override
    public int getOrder() {
        return -200;
    }

    private Mono<Void> setUnauthorizedResponse(ServerWebExchange exchange, String msg) {
        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);

        log.error("[鉴权异常处理]请求路径:{}", exchange.getRequest().getPath());

        return response.writeWith(Mono.fromSupplier(() -> {
            DataBufferFactory bufferFactory = response.bufferFactory();
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("code", HttpStatus.UNAUTHORIZED.value());
            hashMap.put("msg", msg);
            return bufferFactory.wrap(JSON.toJSONBytes(hashMap));
        }));
    }

    /**
     * 获取请求token
     */
    private String getToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(token) && token.startsWith(TOKEN_PREFIX)) {
            token = token.replace(TOKEN_PREFIX, "");
        }
        return token;
    }

    private String getTokenKey(String token) {
        return LOGIN_TOKEN_KEY + token;
    }
}
