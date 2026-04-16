package com.zsy.backend_java.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import com.zsy.backend_java.dto.UserDTO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import static com.zsy.backend_java.util.RedisConstants.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        // Check if is necessary to intercept
        if(UserHolder.getUser() == null) {
            // User not exists
            response.setStatus(401);
            return false;
        }
        // User exists
        return true; // continue request
        
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // No post-processing needed
        UserHolder.removeUser(); // 清除ThreadLocal中的用户信息
    }

}
