package com.zsy.backend_java.service.impl;

import static com.zsy.backend_java.util.RedisConstants.CACHE_NULL_TTL;
import static com.zsy.backend_java.util.RedisConstants.CACHE_SHOP_KEY;
import static com.zsy.backend_java.util.RedisConstants.CACHE_SHOP_TTL;
import static com.zsy.backend_java.util.RedisConstants.LOCK_SHOP_KEY;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.webresources.Cache;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.Shop;
import com.zsy.backend_java.mapper.ShopMapper;
import com.zsy.backend_java.service.IShopService;
import com.zsy.backend_java.util.CacheClient;
import com.zsy.backend_java.util.RedisData;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private CacheClient cacheClient;
    
    @Override
    public Result queryById(Long id) {
        // 1. in cache pass through case/ cache penetration
        // Shop shop = cacheClient
        // .queryWithPassThrough(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // mutex 
        // Shop shop = queryWithMutex(id);

        // 2. In logical expiration time to resolve hot key cache breakdown issue 
        Shop shop = cacheClient.queryWithLogicalExpire(CACHE_SHOP_KEY, id, Shop.class, this::getById, CACHE_SHOP_TTL, TimeUnit.MINUTES);
        // Shop shop = queryWithLogicalExpire(id);

        if (shop == null) {
            return Result.fail("Shop doesnt exist!");
        }
        return Result.ok(shop);
    }
    // Thread pool
    /* 
    private static final ExecutorService CACHE_REBUILD_EXECUTOR = Executors.newFixedThreadPool(10);
    public Shop queryWithLogicalExpire(Long id) {
        // 1. Look up from redis cache
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2. Check if shop exists in redis
        // 2.1.1 Shop info not exists, return null
        if (StrUtil.isBlank(shopJson)) {
            return null;
        }
        
        // 2.1.2 hit cache, we need to unserialize json object
        RedisData redisData = JSONUtil.toBean(shopJson, RedisData.class);
        JSONObject data = (JSONObject)redisData.getData();
        Shop shop = JSONUtil.toBean(data, Shop.class);
        LocalDateTime expirTime = redisData.getExpirTime();
        // 3. Check if expired
        if(expirTime.isAfter(LocalDateTime.now())) {
            // 3.1 if not expired, DIRECTLY return shop info
            return shop;
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
                    this.saveSho2Redis(id, 20L);
                    
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
        return shop;
    }
    */
   
    /* 
    public Shop queryWithMutex(Long id) {

        // for tests output
        String threadName = Thread.currentThread().getName();
        System.out.println(threadName + " start query, id=" + id);

        // 1. Look up from redis cache
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2. Check if shop exists in redis
        // 2.1.1 Shop info exists and its not ""
        if (StrUtil.isNotBlank(shopJson)) {
            System.out.println(threadName + " hit cache");
            // Exists, return
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }
        // 2.1.2 check if hit "" 
        if (shopJson != null) {
            System.out.println(threadName + " hit empty cache");
            return null;
        }

        // 3. Reconstruct cache
        // 3.1 Get mutex
        String lockKey = LOCK_SHOP_KEY + id;
        boolean isLock = false;
        // 3.2 Check if success
        try {
            isLock = tryLock(lockKey);
            if (!isLock) {
                // 3.3 if fails, sleep and retry
                Thread.sleep(50);
                return queryWithMutex(id);
            }
            // 3.4 if succedes, lookup database by id
            String key = CACHE_SHOP_KEY + id;
            Shop shop = getById(id);
            // Mock reconstruction delay
            Thread.sleep(200);
            if (shop == null) {
                // write null to redis
                stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
                return null;
            }
            stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
            return shop;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }finally {
            // 4. Release mutex
            if (isLock) {
                unlock(lockKey);
            }
        }
        
        // // 2.2 No exists, look up from database
        // Shop shop = getById(id);
        // String key = CACHE_SHOP_KEY + id;
        // // 2.2.1 Not exists, return warn + write null to redis
        // if (shop == null) {
        //     // write null to redis
        //     stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
        //     return null;
        // }
        // // 2.2.2 Exists, writes to redis
        // stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
    }
    */
    
    /* 
    public Shop queryWithPassThrough(Long id) {
        // 1. Look up from redis cache
        String shopJson = stringRedisTemplate.opsForValue().get(CACHE_SHOP_KEY + id);
        // 2. Check if shop exists in redis
        // 2.1.1 Shop info exists and its not ""
        if (StrUtil.isNotBlank(shopJson)) {
            // Exists, return
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return shop;
        }
        // 2.1.2 check if hit "" 
        if (shopJson != null) {
            return null;
        }
        // 2.2 No exists, look up from database
        Shop shop = getById(id);
        String key = CACHE_SHOP_KEY + id;
        // 2.2.1 Not exists, return warn + write null to redis
        if (shop == null) {
            // write null to redis
            stringRedisTemplate.opsForValue().set(key, "", CACHE_NULL_TTL, TimeUnit.MINUTES);
            return null;
        }
        // 2.2.2 Exists, writes to redis
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop), CACHE_SHOP_TTL, TimeUnit.MINUTES);
        return shop;
    }
    */
    // Get lock
    /* 
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    // Release lock
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
    }
    */
    public void saveSho2Redis(Long id, Long expireSeconds) throws InterruptedException {
        // 1. Lookup 
        Shop shop = getById(id);
        Thread.sleep(200);
        // 2. Logical expiration time
        RedisData redisData = new RedisData();
        redisData.setData(shop);
        redisData.setExpirTime(LocalDateTime.now().plusSeconds(expireSeconds));
        // 3. write to redis
        stringRedisTemplate.opsForValue().set(CACHE_SHOP_KEY + id, JSONUtil.toJsonStr(redisData));
    }

    @Override
    @Transactional
    public Result update(Shop shop) {
        Long id = shop.getId();
        if (id == null) {
            return Result.fail("Shop id is null");
        }
        // 1. update database
        updateById(shop);
        // 2. delete redis cache
        stringRedisTemplate.delete(CACHE_SHOP_KEY + id);
        return Result.ok();
    }
}
