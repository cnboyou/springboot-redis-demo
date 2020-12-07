package com.wyh.redis.cache;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * ClassName: RedisCache
 *
 * @Author: WangYiHai
 * @Date: 2020/12/7 10:19
 * @Description: TODO
 */
@Component
public class RedisStringCache {
    private static final Logger logger = LoggerFactory.getLogger(RedisStringCache.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisStringCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 设置并获取之间的结果，要求key，value都不能为空；如果之前没有值，返回null
     *
     * @param key
     * @param value
     * @return
     */
    public byte[] setAndGetOldValue(String key, String value) {
        return (byte[]) redisTemplate.execute((RedisCallback<byte[]>) con -> con.getSet(key.getBytes(), value.getBytes()));
    }

    public Boolean setValue(String key, String value) {
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) connection -> connection.set(key.getBytes(), value.getBytes()));
    }

    public byte[] getValue(String key) {
        return (byte[]) redisTemplate.execute((RedisCallback<byte[]>) connection -> connection.get(key.getBytes()));
    }

    public Boolean mSetValue(Map<String, String> values) {
        Map<byte[], byte[]> map = new HashMap<>(values.size());
        for (Map.Entry<String, String> entry : values.entrySet()) {
            map.put(entry.getKey().getBytes(), entry.getValue().getBytes());
        }

        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.mSet(map));
    }

    public List<byte[]> mGetValue(List<String> keys) {
        return (List<byte[]>) redisTemplate.execute((RedisCallback<List<byte[]>>) con -> {
            byte[][] bkeys = new byte[keys.size()][];
            for (int i = 0; i < keys.size(); i++) {
                bkeys[i] = keys.get(i).getBytes();
            }
            return con.mGet(bkeys);
        });
    }

    // 自增、自减方式实现计数

    /**
     * 实现计数的加/减（ value为负数表示减）
     *
     * @param key
     * @param value
     * @return 返回redis中的值
     */
    public Long incr(String key, long value) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.incrBy(key.getBytes(), value));
    }

    public Long decr(String key, long value) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.decrBy(key.getBytes(), value));
    }

}