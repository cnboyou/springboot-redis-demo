package com.wyh.redis.cache;

import cn.hutool.core.date.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisStringCommands;
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
public class RedisBitCache {
    private static final Logger logger = LoggerFactory.getLogger(RedisBitCache.class);

    private final RedisTemplate redisTemplate;

    @Autowired
    public RedisBitCache(RedisTemplate redisTemplate) {
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

    // bitmap的测试相关

    public Boolean setBit(String key, Integer index, Boolean tag) {
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.setBit(key.getBytes(), index, tag));
    }

    public Boolean getBit(String key, Integer index) {
        return (Boolean) redisTemplate.execute((RedisCallback<Boolean>) con -> con.getBit(key.getBytes(), index));
    }

    /**
     * 统计bitmap中，value为1的个数，非常适用于统计网站的每日活跃用户数等类似的场景
     *
     * @param key
     * @return
     */
    public Long bitCount(String key) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes()));
    }

    public Long bitCount(String key, int start, int end) {
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitCount(key.getBytes(), start, end));
    }

    /*BITOP 命令支持 AND 、 OR 、 NOT 、 XOR 这四种操作中的任意一种参数：

    BITOP AND destkey srckey1 … srckeyN ，对一个或多个 key 求逻辑与，并将结果保存到 destkey
    BITOP OR destkey srckey1 … srckeyN，对一个或多个 key 求逻辑或，并将结果保存到 destkey
    BITOP XOR destkey srckey1 … srckeyN，对一个或多个 key 求逻辑异或，并将结果保存到 destkey
    BITOP NOT destkey srckey，对给定 key 求逻辑非，并将结果保存到 destkey*/
    public Long bitOp(RedisStringCommands.BitOperation op, String saveKey, String... desKey) {
        byte[][] bytes = new byte[desKey.length][];
        for (int i = 0; i < desKey.length; i++) {
            bytes[i] = desKey[i].getBytes();
        }
        return (Long) redisTemplate.execute((RedisCallback<Long>) con -> con.bitOp(op, saveKey.getBytes(), bytes));
    }

}