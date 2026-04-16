package com.zsy.backend_java.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.zsy.backend_java.util.LoginInterceptor;
import com.zsy.backend_java.util.RefreshTokenInterceptor;

import jakarta.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Login interceptor
        registry.addInterceptor(new LoginInterceptor())
                .excludePathPatterns(
                    "/",
                    "/refresh-token-test.html",
                    "/user/login", 
                    "/user/code", 
                    "/user/code/email",
                    "/blog/hot",
                    "/shop/**",
                    "/shop-type/**",
                    "/upload/**",
                    "/voucher/**"
                ).order(1);
    // Token regresh interceptor
    registry
    .addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate))
    .addPathPatterns("/**")
    .order(0);
    }
}
