package com.zsy.backend_java.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.service.IUserService;

import jakarta.servlet.http.HttpSession;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 实现发送验证码的逻辑
        // 1. 校验手机号
        // 2. 如果不符合，返回错误信息
        // 3. 符合则生成验证码
        // 4. 将验证码保存到session中
        // 5. 发送验证码到用户手机

        // 返回ok
        return Result.ok();
    }
}