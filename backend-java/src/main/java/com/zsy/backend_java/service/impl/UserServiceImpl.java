package com.zsy.backend_java.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zsy.backend_java.dto.LoginFormDTO;
import com.zsy.backend_java.dto.Result;
import com.zsy.backend_java.dto.UserDTO;
import com.zsy.backend_java.entity.User;
import com.zsy.backend_java.mapper.UserMapper;
import com.zsy.backend_java.service.IUserService;
import com.zsy.backend_java.util.RegexUtils;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import static com.zsy.backend_java.util.SystemConstants.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public Result sendCode(String phone, HttpSession session) {
        if (RegexUtils.isPhoneInValid(phone)) {
            return Result.fail("Invalid phone number format");
        }

        String code = RandomUtil.randomNumbers(6);
        session.setAttribute("code", code);
        log.debug("Sending verification code {} to phone number {}", code, phone);
        return Result.ok();
    }

    @Override
    public Result sendCodeEmail(String email, HttpSession session) {
        if (RegexUtils.isEmailInvalid(email)) {
            return Result.fail("Invalid email format");
        }

        String code = RandomUtil.randomNumbers(6);
        session.setAttribute("code", code);
        log.debug("Sending verification code {} to email {}", code, email);
        return Result.ok();
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        String phone = loginForm.getPhone();
        String email = loginForm.getEmail();
        String code = loginForm.getCode();
        Object cacheCode = session.getAttribute("code");

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

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setNickname(user.getNickname());
        userDTO.setIcon(user.getAvatarUrl());
        session.setAttribute("user", userDTO);
        return Result.ok(userDTO);
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
