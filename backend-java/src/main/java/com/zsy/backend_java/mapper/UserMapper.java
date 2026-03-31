package com.zsy.backend_java.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zsy.backend_java.entity.User;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
