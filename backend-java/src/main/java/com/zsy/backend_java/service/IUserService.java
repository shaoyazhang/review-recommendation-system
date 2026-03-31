package com.zsy.backend_java.service;

import com.zsy.backend_java.dto.Result;

import jakarta.servlet.http.HttpSession;

public interface IUserService extends IService<User>{
    /**
     * 发送验证码/Send verification code
     * @param phone 手机号/Phone number
     * @param session 会话/Session
     * @return 结果/Result
     */
    Result sendCode(String phone, HttpSession session);

}
