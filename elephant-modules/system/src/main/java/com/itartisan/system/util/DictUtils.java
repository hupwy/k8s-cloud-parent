package com.itartisan.system.util;


import com.itartisan.common.core.constant.Constants;
import com.itartisan.common.core.utils.SpringUtils;
import com.itartisan.system.domain.SysDictData;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Collection;
import java.util.List;

/**
 * 字典工具类
 */
public class DictUtils {

    private final static String REDISTEMPLATE_BEAN_NAME = "redisTemplate";

    /**
     * 设置字典缓存
     *
     * @param key       参数键
     * @param dictDatas 字典数据列表
     */
    public static void setDictCache(String key, List<SysDictData> dictDatas) {
        SpringUtils.getBean(REDISTEMPLATE_BEAN_NAME, RedisTemplate.class).opsForValue().set(getCacheKey(key), dictDatas);
    }

    /**
     * 获取字典缓存
     *
     * @param key 参数键
     * @return dictDatas 字典数据列表
     */
    public static List<SysDictData> getDictCache(String key) {
        RedisTemplate redisTemplate = SpringUtils.getBean(REDISTEMPLATE_BEAN_NAME, RedisTemplate.class);
        ValueOperations<String, Object> operation = redisTemplate.opsForValue();
        Object cacheObj = operation.get(getCacheKey(key));
        if (cacheObj != null) {
            List<SysDictData> dictDatas = (List<SysDictData>) cacheObj;
            return dictDatas;
        }
        return null;
    }

    /**
     * 清空字典缓存
     */
    public static void clearDictCache() {
        RedisTemplate redisTemplate = SpringUtils.getBean(REDISTEMPLATE_BEAN_NAME, RedisTemplate.class);
        Collection<String> keys = redisTemplate.keys(Constants.SYS_DICT_KEY + "*");
        redisTemplate.delete(keys);
    }

    /**
     * 设置cache key
     *
     * @param configKey 参数键
     * @return 缓存键key
     */
    public static String getCacheKey(String configKey) {
        return Constants.SYS_DICT_KEY + configKey;
    }
}
