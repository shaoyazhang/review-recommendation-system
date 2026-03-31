package com.zsy.backend_java.util;

public class RegexPatterns {
    // 手机号正则表达式/Phone number regex pattern
    // public static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    public static final String PHONE_REGEX = "^[67]\\d{8}$";
    // 邮箱正则表达式/Email regex pattern
    public static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    // 验证码正则表达式/Verification code regex pattern
    /* 验证码必须是6个数字 / Verification code must be 6 digits */
    public static final String VERIFY_CODE_REGEX = "^\\d{6}$";
    // 密码正则表达式/Password regex pattern
    /**
     * (?=.*[a-z]) → 至少一个小写字母 / At least one lowercase letter
     * (?=.*[A-Z]) → 至少一个大写字母 / At least one uppercase letter
     * (?=.*\\d) → 至少一个数字 / At least one digit
     * (?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]) → 至少一个特殊字符 / At least one special character
     * .{8,} → 最少8个字符 / At least 8 characters
     */
    public static final String PASSWORD_REGEX =
    "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-={}\\[\\]:;\"'<>,.?/]).{8,}$";
}
