package com.zsy.backend_java.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.ShopType;
import com.zsy.backend_java.mapper.ShopTypeMapper;
import com.zsy.backend_java.service.IShopeTypeService;

import cn.hutool.json.JSONUtil;

import static com.zsy.backend_java.util.RedisConstants.*;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Resource;

@Service
public class ShopTypeServiceImpl extends ServiceImpl<ShopTypeMapper, ShopType> implements IShopeTypeService{

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result getTypeList() {
        String typeKey = CACHE_TYPE_KEY;

        // First lookup from redis
        Long typeListSize = stringRedisTemplate.opsForList().size(typeKey);
        // If exists in redis
        if (typeListSize!=null && typeListSize!=0) {
            List<String> typeJsonList = stringRedisTemplate.opsForList().range(typeKey, 0, typeListSize - 1);
            ArrayList<ShopType> typeList = new ArrayList<>();
            for (String typeJson : typeJsonList) {
                typeList.add(JSONUtil.toBean(typeJson, ShopType.class));
            }
            return Result.ok(typeList);
        }
        // No exists in redis -> lookup from database
        List<ShopType> shopTypeList = query().orderByAsc("sort").list();
        // Not exist in database -> shop not exists
        if (shopTypeList == null) {
            // Shop not exists in the database
            return Result.fail("Shop doesnt exist");
        }
        // shop exists -> write to redis first
        // 1. convert to Json
        ArrayList<String> typeJsonList = new ArrayList<>();
        for (ShopType shopType : shopTypeList) {
            typeJsonList.add(JSONUtil.toJsonStr(shopType));
        }
        // 2. write to redis
        stringRedisTemplate.opsForList().rightPushAll(typeKey, typeJsonList);
        return Result.ok(shopTypeList);
    }

    
}
