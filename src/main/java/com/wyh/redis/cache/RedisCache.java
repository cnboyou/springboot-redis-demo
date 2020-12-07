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
public class RedisCache {
    private static final Logger logger = LoggerFactory.getLogger(RedisCache.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisCache(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /*应用场景： 打卡签到（1 签到、0 未签到）*/
    /**
     * 二值状态统计 bit
     * key: 用户id+月份
     * value: 天数
     */
    public void setUserSign(Integer userId, Date date) {
        int year = DateUtil.year(date);
        int month = DateUtil.month(date);
        int day = DateUtil.dayOfMonth(date);
        redisTemplate.opsForValue().setBit("user:id:" + userId + ":" +year+month, day - 1,true);
    }

    /**
     * 获取某天是否打卡
     * @param userId
     * @param date
     * @return
     */
    public Boolean getUserSignByDate(Integer userId, Date date) {
        int year = DateUtil.year(date);
        int month = DateUtil.month(date);
        int day = DateUtil.dayOfMonth(date);
        String key = "user:id:" + userId + ":" + year + month;
        if (redisTemplate.hasKey(key)) {
            return redisTemplate.opsForValue().getBit(key, day - 1);
        }
        return null;
    }

    /**
     * 统计月份的打卡次数
     * @param userId
     * @param date
     * @return
     */
    public Long countUserSignByMonth(Integer userId, Date date) {
        int year = DateUtil.year(date);
        int month = DateUtil.month(date);
        Date dateTime = DateUtil.endOfMonth(date);
        String key = "user:id:" + userId + ":" + year + month;
        return (Long) redisTemplate.execute((RedisCallback<Long>) redisConnection
                -> redisConnection.bitCount(key.getBytes()));
    }

    /*聚合统计*/
    /**
     * 交集
     * 案例：统计双方的共同好友
     */
    public Set getCommonFriend(Integer userId1, Integer userId2) {
        String key1 = "user:id:f:"+userId1;
        String key2 = "user:id:f:"+userId2;
        String key3 = "user:id:f:" + userId1 + userId2;
        redisTemplate.opsForSet().differenceAndStore(key3, userId1, userId2);
        Set members = redisTemplate.opsForSet().members(key1);
        Set members1 = redisTemplate.opsForSet().members(key2);
        return redisTemplate.opsForSet().members(key3);
    }

    /*排序统计*/
    /**
     * 案例：商品的评论总是最新的在上面
     */
    public void sort(Integer userId1, String content) {
        String key1 = "user:id:c:"+userId1;
        Boolean add = redisTemplate.opsForZSet().add(key1, content, 1);
    }

}