package com.zsy.backend_java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.Shop;

public interface IShopService extends IService<Shop>{

    Result queryById(Long id);

    Result update(Shop shop);

    Result queryShopByType(Integer typeId, Integer current, Double x, Double y);
}
