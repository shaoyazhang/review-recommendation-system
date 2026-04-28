package com.zsy.backend_java.util;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class RedisData {
    private LocalDateTime expirTime;
    private Object data;
}
