package com.zsy.backend_java.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.Shop;
import com.zsy.backend_java.mapper.ShopMapper;
import com.zsy.backend_java.service.IShopService;
import static com.zsy.backend_java.util.RedisConstants.*;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import jakarta.annotation.Resource;

@Service
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements IShopService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result queryById(Long id) {
        // 1. Look up from redis cache
        String shopJson = stringRedisTemplate.opsForValue().get("cache:shop" + id);
        // 2. Check if shop exists in redis
        if (StrUtil.isNotBlank(shopJson)) {
            // 2.1 Exists, return
            Shop shop = JSONUtil.toBean(shopJson, Shop.class);
            return Result.ok(shop);
        }
        // 2.2 No exists, look up from database
        Shop shop = getById(id);
        // 2.2.1 Exists, writes to redis
        String key = CACHE_SHOP_KEY + id;
        stringRedisTemplate.opsForValue().set(key, JSONUtil.toJsonStr(shop));
        // 2.2.2 Not exists, return warn
        if (shop == null) {
            return Result.fail("Restaurant not exists!");
        }

        return Result.ok(shop);
    }

}
