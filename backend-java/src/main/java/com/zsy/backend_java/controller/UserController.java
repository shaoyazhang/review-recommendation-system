package com.zsy.backend_java.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zsy.backend_java.dto.LoginFormDTO;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.service.IUserService;
import com.zsy.backend_java.util.RegexUtils;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;


@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private IUserService userService;

    // @Resource
    // private IUserInfoService userInfoService;

    /**
     * 发送验证码/Send verification code
     */

    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // 发送短信验证码并保存验证码
        
        return userService.sendCode(phone, session);
    }

    @PostMapping("code/email")
    public Result sendCodeEmail(@RequestParam("email") String email, HttpSession session) {
        // 发送邮箱验证码并保存验证码
        
        return userService.sendCodeEmail(email, session);
    }
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        //TODO: process POST request
        
        String phone = loginForm.getPhone();
        Object cacheCode = session.getAttribute("code");
        String code = loginForm.getCode();
        String email = loginForm.getEmail();
        String password = loginForm.getPassword();
        // ❗ 防止用户乱传（两个都传）
        if (phone != null && email != null) {
            return Result.fail("Please provide either phone or email, not both");
        }
        // 1. 校验手机号
        if (phone != null) {
            if (RegexUtils.isPhoneInValid(phone)) {
                return Result.fail("Invalid phone number format");
            }
        }

        // 2. 校验邮箱
        if (email != null) {
            if (RegexUtils.isEmailInvalid(email)) {
                return Result.fail("Invalid email format");
            }
        }
        // 3. 校验验证码
        if (cacheCode == null || !cacheCode.toString().equals(code)) {
            return Result.fail("Invalid verification code");
        }

        // 3. 符合则登录成功，返回用户信息
        return userService.login(loginForm, session);
    }
    
}
    