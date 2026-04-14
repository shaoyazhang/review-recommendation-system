package com.zsy.backend_java.util;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import com.zsy.backend_java.dto.UserDTO;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import static com.zsy.backend_java.util.RedisConstants.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public LoginInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response, Object handler) throws Exception {
        // 1. get session
        // HttpSession session = request.getSession();
        // 1. Get token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)) {
            response.setStatus(401);
            return false;
        }
        // 2. get user from session 
        // Object user = session.getAttribute("user");
        // 2. get user from token
        Map<Object,Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        // 3. Check if user exists
        // if (user == null) {
        //     // 4. Not exists, return 401 status code
        //     response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //     return false; // 拦截请求
        // }
        if (userMap.isEmpty()) {
            response.setStatus(401);
            return false;
        }
        // 4. Convert User to UserDTO
        UserDTO user = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 5. Exists，save user info to ThreadLocal
        UserHolder.saveUser(user);
        // 6. Refresh token expiration time
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
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
