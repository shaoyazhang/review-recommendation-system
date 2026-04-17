package com.zsy.backend_java.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.Shop;
import com.zsy.backend_java.mapper.ShopMapper;
import com.zsy.backend_java.service.IShopService;
import static com.zsy.backend_java.util.RedisConstants.*;

import java.sql.Time;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {
        // cache pass through
        // Shop shop = queryWithPassThrough(id);
        // mutex 

        return Result.ok(shop);
    }

    public Shop queryWithMutex(Long id) {
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
    // Get lock
    private boolean tryLock(String key) {
        Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, "1", 10, TimeUnit.SECONDS);
        return BooleanUtil.isTrue(flag);
    }
    // Release lock
    private void unlock(String key) {
        stringRedisTemplate.delete(key);
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
