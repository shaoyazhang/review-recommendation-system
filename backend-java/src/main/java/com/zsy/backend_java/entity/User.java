package com.zsy.backend_java.entity;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("tb_user")
public class User {
    private static final long serialVersionUID = 1L;
    /**
     * 主键/Primary key
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 手机号码/Phone number
     */
    private String phone;

    /**
     * 邮箱/Email
     */
    private String email;

    /**
     * 密码，加密存储/Password, encrypted
     */ 
    private String password;

    /**
     * 昵称/Nickname
     */
    private String nickname;
    /**
     * 头像/Avatar
     */    
    private String avatarUrl = "";
    /**
     * 创建时间/Create time
     */
    private LocalDateTime createTime;

    /**
     * 更新时间/Update time
     */
    private LocalDateTime updateTime;
}
