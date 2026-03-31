package com.zsy.backend_java.util;
import cn.hutool.core.util.StrUtil;

public class RegexUtils {
    /**
     * 验证手机号是否合法/Validate if the phone number is valid
     * @param phone 手机号/Phone number
     * @return 是否合法/Is valid
     */
    public static boolean isPhoneValid(String phone) {
        return mismatch(phone, RegexPatterns.PHONE_REGEX);
    }

    /**
     * 验证邮箱是否合法/Validate if the email is valid
     * @param email 邮箱/Email
     * @return 是否合法/Is valid
     */
    public static boolean isEmailValid(String email) {
        return mismatch(email, RegexPatterns.EMAIL_REGEX);
    }


    /**
     * 是否是无效验证码格式/Check if the code format is invalid
     * @param code 验证码/Verification code
     * @return 是否无效/Is invalid
     */
    public static boolean isCodeInvalid(String code) {
        return mismatch(code, RegexPatterns.VERIFY_CODE_REGEX);
    }
    // 校验是否不符合正则格式 / Check if the string does not match the regex format
    private static boolean mismatch(String str, String regex) {
        if (StrUtil.isBlank(str)) {
            return true;
        }
        return !str.matches(regex);
    }
}
