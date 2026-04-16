package com.zsy.backend_java.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.service.IShopeTypeService;

import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/shop-type")
public class ShopTypeController {
    @Resource
    private IShopeTypeService typeService;

    @GetMapping("/list")
    public Result queryTypeList() {
        return typeService.getTypeList();
    }
    
}
