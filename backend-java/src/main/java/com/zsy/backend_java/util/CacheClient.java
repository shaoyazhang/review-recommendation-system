package com.zsy.backend_java.util;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import com.zsy.backend_java.entity.Shop;

import static com.zsy.backend_java.util.RedisConstants.*;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CacheClient {
    private final StringRedisTemplate stringRedisTemplate;
    
    public CacheClient(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void set(String key, Object value, Long time, TimeUnit unit) {
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(value), time, unit);
    }
    /**
     * Set with logical expiration time, write to redis, solve cache pass through issue
     * @param key
     * @param value
     * @param time
     * @param unit
     */
    public void setWithLogicalExpire(String key, Object value, Long time, TimeUnit unit) {
        // Configure logical expiration time
        RedisData redisData = new RedisData();
        redisData.setData(value); 
        redisData.setExpirTime(LocalDateTime.now().plusSeconds(unit.toSeconds(time)));
        // Write to redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(redisData));
    }
    /**
     * Query with pass through, solve cache pass through issue
     * @param <R>
     * @param <ID>
     * @param keyPrefix
     * @param id
     * @param type
     * @param dbFallback
     * @param time
     * @param unit
     * @return R
     */
    public <R, ID> R queryWithPassThrough(
        String keyPrefix,
        ID id, 
        Class<R> type,
        Function<ID, R> dbFallback,
        Long time,
        TimeUnit unit) {

        String key = keyPrefix + id;
        // 1. Look up from redis cache
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2. Check if shop exists in redis
        // 2.1.1 Shop info exists and its not ""
        if (StrUtil.isNotBlank(json)) {
            // Exists, return
            return JSONUtil.toBean(json, type);
        }

        // 2.1.2 check if hit "" 
        if (json != null) {
            return null;
        }
        // 2.2 No exists, look up from database
        R r = dbFallback.apply(id);
        // 2.2.1 Not exists, return warn + write null to redis
        if (r == null) {
            // write null to redis
            this.set(key, "", CACHE_NULL_TTL, TimeUnit.SECONDS);
            return null;
        }
        // 2.2.2 Exists, writes to redis
        this.set(key, r, time, unit);
        return r;
    }
    

    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    public <R, ID> R queryWithLogicalExpire(
        String keyPrefix,
        ID id,
        Class<R> type,
        Function<ID, R> dbFallback, // ID -> parameter, R -> return type
        Long time,
        TimeUnit unit) {

        String key = keyPrefix + id;
        // 1. Look up from redis cache
        String json = stringRedisTemplate.opsForValue().get(key);
        // 2. Check if shop exists in redis
        // 2.1.1 Shop info not exists, return null
        if (StrUtil.isBlank(json)) {
            return null;
        }
        
        // 2.1.2 hit cache, we need to unserialize json object
        RedisData redisData = JSONUtil.toBean(json, RedisData.class);
        JSONObject data = (JSONObject)redisData.getData();
        R r = JSONUtil.toBean(data, type);
        LocalDateTime expirTime = redisData.getExpirTime();
        // 3. Check if expired
        if(expirTime.isAfter(LocalDateTime.now())) {
            // 3.1 if not expired, DIRECTLY return shop info
            return r;
        }
        // 3.2 if expired, need to store in cache by using mutex
        // 4. store in cache
        // 4.1 get mutex
        String lockKey = LOCK_SHOP_KEY + id;
        // 4.2 check if mutex obtained
        boolean isLock = tryLock(lockKey);
        // 4.2.1 yes, new thread to store in cache
        if (isLock) {
            // Start new thread, store in cache
            CACHE_REBUILD_EXECUTOR.submit(()->{
                try {
                    R newR =dbFallback.apply(id);
                    // write to redis
                    this.setWithLogicalExpire(key, newR, time, unit);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } finally {
                    // 4. Release lock
                    if (isLock) {
                        unlock(lockKey);
                    }
                }

            });
        }
        // 4.2.1 return shop info (either updated or old info)
        return r;
    }

    // Get lock
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    // Release lock
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
}
