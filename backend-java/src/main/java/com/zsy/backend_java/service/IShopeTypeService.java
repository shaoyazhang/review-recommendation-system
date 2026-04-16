package com.zsy.backend_java.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.ShopType;

public interface IShopeTypeService extends IService<ShopType>{
    Result getTypeList();
}
