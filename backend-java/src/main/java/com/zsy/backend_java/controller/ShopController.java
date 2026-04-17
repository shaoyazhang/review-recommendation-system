package com.zsy.backend_java.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.Shop;
import com.zsy.backend_java.service.IShopService;
import com.zsy.backend_java.util.SystemConstants;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/shop")
public class ShopController {

    @Resource
    public IShopService shopService;

    @GetMapping("/{id}")
    public Result queryShoById(@PathVariable("id")  Long id) {
        return shopService.queryById(id);
    }

    @PostMapping
    public Result saveShop(@RequestBody Shop shop) {
        shopService.save(shop);
        return Result.ok();
    }

    @PutMapping
    public Result updateShop(@RequestBody Shop shop) {
        return shopService.update(shop);
    }

    @GetMapping("/of/type")
    public Result queryShopByType(
        @RequestParam(value = "typeId") Integer typeId,
        @RequestParam(value = "current", defaultValue = "1") Integer current,
        @RequestParam(value = "x", required = false) Double x,
        @RequestParam(value = "y", required = false) Double y
        ) {
        return shopService.queryShopByType(typeId, current, x, y);
    }

    @GetMapping("/of/name")
    public Result queryShopByName(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "current", defaultValue = "1") Integer current
        ) {
        // Lookup by pages based on category
        Page<Shop> page = shopService.query()
            .like(StrUtil.isNotBlank(name), "name", name)
            .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        return Result.ok(page.getRecords());
    }
    
    
}
