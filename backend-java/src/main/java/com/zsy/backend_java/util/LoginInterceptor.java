package com.zsy.backend_java.util;

import org.springframework.web.servlet.HandlerInterceptor;

import com.zsy.backend_java.dto.UserDTO;
import com.zsy.backend_java.entity.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        // 1. 获取session
        HttpSession session = request.getSession();
        // 2. 获取session中的用户
        Object user = session.getAttribute("user");
        // 3. 判断用户是否存在
        if (user == null) {
            // 4. 不存在，拦截请求，返回401状态码
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false; // 拦截请求
        }
        // 5. 存在，保存用户信息到ThreadLocal中
        UserHolder.saveUser((UserDTO) user);
        return true; // 继续处理请求
        
    }

    @Override
    public void postHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler,
            org.springframework.web.servlet.ModelAndView modelAndView) throws Exception {
        // No post-processing needed
        UserHolder.removeUser(); // 清除ThreadLocal中的用户信息
    }

}
