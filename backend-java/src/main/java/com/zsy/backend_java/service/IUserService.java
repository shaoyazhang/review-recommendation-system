package com.zsy.backend_java.service;

import com.zsy.backend_java.dto.LoginFormDTO;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpSession;

public interface IUserService extends IService<User>{
    /**
     * 发送验证码/Send verification code
     * @param phone 手机号/Phone number
     * @param session 会话/Session
     * @return 结果/Result
     */
    Result sendCode(String phone, HttpSession session);
    /**
     * 发送邮箱验证码/Send email verification code
     * @param email 邮箱/Email
     * @param session 会话/Session
     * @return 结果/Result
     */
    Result sendCodeEmail(String email, HttpSession session);
    /**
     * 用户登录/User login
     * @param loginForm 登录表单/Login form
     * @param session 会话/Session
     * @return 结果/Result
     */
    Result login(LoginFormDTO loginForm, HttpSession session);
}


