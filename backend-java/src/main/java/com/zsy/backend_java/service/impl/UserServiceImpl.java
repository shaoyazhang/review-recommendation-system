package com.zsy.backend_java.service.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.LoginFormDTO;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.dto.UserDTO;
import com.zsy.backend_java.entity.User;
import com.zsy.backend_java.mapper.UserMapper;
import com.zsy.backend_java.service.IUserService;
import com.zsy.backend_java.util.RegexUtils;
import static com.zsy.backend_java.util.RedisConstants.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import static com.zsy.backend_java.util.SystemConstants.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public Result sendCodePhone(String phone, HttpSession session) {
        if (RegexUtils.isPhoneInValid(phone)) {
            return Result.fail("Invalid phone number format");
        }

        String code = RandomUtil.randomNumbers(6);
        // Save code in session
        // session.setAttribute("code", code);
        // Save verification code to redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        log.debug("Sending verification code {} to phone number {}", code, phone);
        return Result.ok();
    }

    @Override
    public Result sendCodeEmail(String email, HttpSession session) {
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.fail("Invalid email format");
        }

        String code = RandomUtil.randomNumbers(6);
        // session.setAttribute("code", code);
        // Save verification code to redis
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + email, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);
        log.debug("Sending verification code {} to email {}", code, email);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();
        String email = loginForm.getEmail();
        String code = loginForm.getCode();
        // Object cacheCode = session.getAttribute("code");
        String key;
        if (StrUtil.isNotBlank(phone)) {
            key = LOGIN_CODE_KEY + phone;
        } else {
            key = LOGIN_CODE_KEY + email;
        }

        // Obtain verification code from redis
        String cacheCode = stringRedisTemplate.opsForValue().get(key);       

        if (StrUtil.isNotBlank(phone) && StrUtil.isNotBlank(email)) {
            return Result.fail("Please provide either phone or email, not both");
        }

        if (StrUtil.isBlank(phone) && StrUtil.isBlank(email)) {
            return Result.fail("Phone or email is required");
        }

        if (StrUtil.isNotBlank(phone) && RegexUtils.isPhoneInValid(phone)) {
            return Result.fail("Invalid phone number format");
        }

        if (StrUtil.isNotBlank(email) && RegexUtils.isEmailInvalid(email)) {
            return Result.fail("Invalid email format");
        }

        if (cacheCode == null || !cacheCode.toString().equals(code)) {
            return Result.fail("Invalid verification code");
        }

        User user;
        if (StrUtil.isNotBlank(phone)) {
            user = query().eq("phone", phone).one();
            if (user == null) {
                user = createUserWithPhone(phone);
            }
            
        } else {
            user = query().eq("email", email).one();
            if (user == null) {
                user = createUserWithEmail(email);
            }
        }

        // Save user information to session
        // session.setAttribute("user", BeanUtil.copyProperties(user, UserDTO.class));
        // Save user info to redis
        // 1. Generate token
        String token = UUID.randomUUID().toString(true);
        // 2. Hash user object
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        Map<String,Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>()
                , CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue == null ? null : fieldValue.toString())
                    );
        // Store in redis
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY + token, userMap);
        // Set up expire time
        stringRedisTemplate.expire(LOGIN_USER_KEY + token, LOGIN_USER_TTL, TimeUnit.MINUTES);
        return Result.ok(token);
    }

    private User createUserWithPhone(String phone) {
        User user = new User();
        user.setPhone(phone);
        user.setNickname(USER_NICKNAME_PREFIX + RandomUtil.randomString(10));
        save(user);
        return user;
    }

    private User createUserWithEmail(String email) {
        User user = new User();
        user.setEmail(email);
        user.setNickname(USER_NICKNAME_PREFIX + RandomUtil.randomString(10));
        save(user);
        return user;
    }
}
