package com.zsy.backend_java.service.impl;

import java.util.Random;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.LoginFormDTO;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.User;
import com.zsy.backend_java.mapper.UserMapper;
import com.zsy.backend_java.service.IUserService;
import com.zsy.backend_java.util.RegexUtils;

import cn.hutool.core.util.RandomUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    
    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 实现发送验证码的逻辑
        // 1. 校验手机号
        // 2. 如果不符合，返回错误信息
        if (RegexUtils.isPhoneInValid(phone)) {
            return Result.fail("Invalid phone number format");
        }
        
        // 3. 符合则生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4. 将验证码保存到session中
        session.setAttribute("code", code);
        // 5. 发送验证码到用户手机
        log.debug("Sending verification code {} to phone number {}", code, phone);
        // 返回ok
        return Result.ok();
    }

    @Override
    public Result sendCodeEmail(String email, HttpSession session) {
        // 实现发送验证码的逻辑
        // 1. 校验邮箱
        // 2. 如果不符合，返回错误信息
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.fail("Invalid email format");
        }
        
        // 3. 符合则生成验证码
        String code = RandomUtil.randomNumbers(6);
        // 4. 将验证码保存到session中
        session.setAttribute("code", code);
        // 5. 发送验证码到用户邮箱
        log.debug("Sending verification code {} to email {}", code, email);
        // 返回ok
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        return null;
    }
}