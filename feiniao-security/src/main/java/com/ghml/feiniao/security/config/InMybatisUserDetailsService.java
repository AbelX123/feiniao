package com.ghml.feiniao.security.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.entity.UserEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.UserMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Objects;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 以mybatis plus加载用户数据
 */
@Service
public class InMybatisUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    // 构造器注入
    public InMybatisUserDetailsService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 封装查询条件
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        UserEntity userEntity = userMapper.selectOne(wrapper);
        if (Objects.isNull(userEntity)) {
            throw new ServiceException(Code.USER_PASSWORD_NOT_MATCH);
        }
        return new MyUserDetails(userEntity.getUserId(), username, userEntity.getPassword(), Collections.emptySet());
    }
}
