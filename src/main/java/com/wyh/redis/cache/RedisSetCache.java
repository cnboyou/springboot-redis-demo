package com.wyh.redis.cache;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

/**
 * ClassName: RedisCache
 *
 * @Author: WangYiHai
 * @Date: 2020/12/7 10:19
 * @Description: TODO
 */
@Component
public class RedisSetCache {
    private static final Logger logger = LoggerFactory.getLogger(RedisSetCache.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisSetCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*聚合统计*/
    /**
     * 交集
     * 案例：统计双方的共同好友
     */
    public Set getCommonFriend(Integer userId1, Integer userId2) {
        String key1 = "user:id:f:"+userId1;
        String key2 = "user:id:f:"+userId2;
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 返回多个集合的交集 sinter
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<String> intersect(String key1, String key2) {
        return redisTemplate.opsForSet().intersect(key1, key2);
    }

    /**
     * 返回多个集合的并集  sunion
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<String> union(String key1, String key2) {
        return redisTemplate.opsForSet().union(key1, key2);
    }

    /**
     * 返回集合key1中存在，但是key2中不存在的数据集合  sdiff
     *
     * @param key1
     * @param key2
     * @return
     */
    public Set<String> diff(String key1, String key2) {
        return redisTemplate.opsForSet().difference(key1, key2);
    }

    /*排序统计*/
    /**
     * 案例：商品的评论总是最新的在上面
     */
    public void sort(Integer userId1, String content) {
        String key1 = "user:id:c:"+userId1;
        Boolean add = redisTemplate.opsForZSet().add(key1, content, 1);
    }


    /**
     * 新增一个  sadd
     *
     * @param key
     * @param value
     */
    public void add(String key, String value) {
        redisTemplate.opsForSet().add(key, value);
    }

    /**
     * 删除集合中的值  srem
     *
     * @param key
     * @param value
     */
    public void remove(String key, String value) {
        redisTemplate.opsForSet().remove(key, value);
    }

    /**
     * 判断是否包含  sismember
     *
     * @param key
     * @param value
     */
    public void contains(String key, String value) {
        redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 获取集合中所有的值 smembers
     *
     * @param key
     * @return
     */
    public Set<String> values(String key) {
        return redisTemplate.opsForSet().members(key);
    }


}