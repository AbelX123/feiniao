package com.ghml.feiniao.common.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description Redis业务流程通用服务
 */
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    // 构造器注入 RedisTemplate
    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // 存储
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    // 存储（设置过期时间）
    public void setExpMillis(String key, Object value, Long exp) {
        redisTemplate.opsForValue().set(key, value, exp, TimeUnit.MILLISECONDS);
    }

    // 批量存储
    public void multiSet(Map<String, Object> map) {
        redisTemplate.opsForValue().multiSet(map);
    }

    // 获取
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 获取set元素
    public Set<Object> members(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    // 自增
    public Long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    // 自减
    public Long decrement(String key) {
        return redisTemplate.opsForValue().increment(key, -1);
    }

    // 设置过期时间（毫秒）
    public Boolean expireMillis(String key, long timeoutMillis) {
        return redisTemplate.expire(key, timeoutMillis, TimeUnit.MILLISECONDS);
    }

    // 删除
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    // 批量删除
    public Long deleteBatch(String... keys) {
        return redisTemplate.delete(new HashSet<>(Arrays.asList(keys)));
    }

    // 事务操作
    // @param sessionCallback 事务执行的回调函数
    public Object executeTransaction(SessionCallback<Object> sessionCallback) {
        return redisTemplate.execute(sessionCallback);
    }
}