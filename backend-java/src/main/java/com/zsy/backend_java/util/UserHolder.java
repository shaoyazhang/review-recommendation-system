package com.zsy.backend_java.util;

import com.zsy.backend_java.dto.UserDTO;

public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user) {
        tl.set(user);
    }

    public static UserDTO getUser() {
        return tl.get();
    }
    public static void removeUser() {
        tl.remove();
    }
}
