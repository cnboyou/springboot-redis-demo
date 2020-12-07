package com.wyh.redis;

import cn.hutool.core.date.DateUtil;
import com.wyh.redis.cache.RedisCache;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Set;

/**
 * ClassName: RedisTest
 *
 * @Author: WangYiHai
 * @Date: 2020/12/7 10:33
 * @Description: TODO
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        Date dateTime = DateUtil.offsetDay(new Date(), -4);
        System.out.println(dateTime);
        redisCache.setUserSign(10001, dateTime);
        Boolean userSign = redisCache.getUserSignByDate(10001, dateTime);
        System.out.println(userSign);
        Long integer = redisCache.countUserSignByMonth(10001, dateTime);
        System.out.println(integer);
    }

    @Test
    public void test1() {
        String key = "user:id:f:" + 1001;
        String key2 = "user:id:f:" + 1002;
        redisTemplate.opsForSet().add(key, 100);
        redisTemplate.opsForSet().add(key, 101);
        redisTemplate.opsForSet().add(key, 102);
        redisTemplate.opsForSet().add(key2, 101);
        redisTemplate.opsForSet().add(key2, 102);
        redisTemplate.opsForSet().add(key2, 103);

        Set friend = redisCache.getCommonFriend(1001, 1002);
        System.out.println(friend);
    }

}