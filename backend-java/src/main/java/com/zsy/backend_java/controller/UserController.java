package com.zsy.backend_java.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zsy.backend_java.dto.LoginFormDTO;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.dto.UserDTO;
import com.zsy.backend_java.service.IUserService;
import com.zsy.backend_java.util.UserHolder;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;



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
        
        return userService.login(loginForm, session);
    }


    @GetMapping("/me")
    public Result me() {
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }
    
    
}
    