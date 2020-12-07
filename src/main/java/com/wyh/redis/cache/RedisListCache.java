package com.wyh.redis.cache;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * ClassName: RedisCache
 *
 * @Author: WangYiHai
 * @Date: 2020/12/7 10:19
 * @Description: TODO
 */
@Component
public class RedisListCache {
    private static final Logger logger = LoggerFactory.getLogger(RedisListCache.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisListCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 在列表的最左边塞入一个value
     *
     * @param key
     * @param value
     */
    public void lpush(String key, String value) {
        redisTemplate.opsForList().leftPush(key, value);
    }

    /**
     * 获取指定索引位置的值, index为-1时，表示返回的是最后一个；当index大于实际的列表长度时，返回null
     *
     * @param key
     * @param index
     * @return
     */
    public String index(String key, int index) {
        return (String) redisTemplate.opsForList().index(key, index);
    }

    /**
     * 获取范围值，闭区间，start和end这两个下标的值都会返回; end为-1时，表示获取的是最后一个；
     *
     * 如果希望返回最后两个元素，可以传入  -2, -1
     *
     * @param key
     * @param start
     * @param end
     * @return
     */
    public List<String> range(String key, int start, int end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 返回列表的长度
     *
     * @param key
     * @return
     */
    public Long size(String key) {
        return redisTemplate.opsForList().size(key);
    }

    /**
     * 设置list中指定下标的值，采用干的是替换规则, 最左边的下标为0；-1表示最右边的一个
     *
     * @param key
     * @param index
     * @param value
     */
    public void set(String key, Integer index, String value) {
        redisTemplate.opsForList().set(key, index, value);
    }

    /**
     * 删除列表中值为value的元素，总共删除count次；
     *
     * 如原来列表为 【1， 2， 3， 4， 5， 2， 1， 2， 5】
     * 传入参数 value=2, count=1 表示删除一个列表中value为2的元素
     * 则执行后，列表为 【1， 3， 4， 5， 2， 1， 2， 5】
     *
     * @param key
     * @param value
     * @param count
     */
    public void remove(String key, String value, int count) {
        redisTemplate.opsForList().remove(key, count, value);
    }

    /**
     * 删除list首尾，只保留 [start, end] 之间的值
     *
     * @param key
     * @param start
     * @param end
     */
    public void trim(String key, Integer start, Integer end) {
        redisTemplate.opsForList().trim(key, start, end);
    }




}