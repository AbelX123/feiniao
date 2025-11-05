package com.ghml.feiniao.security.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.entity.RoleEntity;
import com.ghml.feiniao.common.entity.UserEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.RoleMapper;
import com.ghml.feiniao.common.mapper.UserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 以mybatis plus加载用户数据
 */
@Service
public class InMybatisUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    // 构造器注入
    public InMybatisUserDetailsService(UserMapper userMapper,
                                       RoleMapper roleMapper) {
        this.userMapper = userMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        // 查询用户
        LambdaQueryWrapper<UserEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserEntity::getUsername, username);
        UserEntity user = userMapper.selectOne(wrapper);
        if (Objects.isNull(user)) {
            throw new ServiceException(Code.USER_PASSWORD_NOT_MATCH);
        }
        return new MyUserDetails(user.getUserId(), username, user.getPassword(), Collections.emptySet());
    }

    public UserDetails loadUserByUserId(String userId) {
        // 查询用户
        UserEntity user = userMapper.selectById(userId);
        if (Objects.isNull(user)) {
            throw new ServiceException(Code.USER_PASSWORD_NOT_MATCH);
        }
        // 查询权限(查询角色，用户和角色是一对一关系)
        RoleEntity role = roleMapper.selectById(user.getRoleId());
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getRoleCode()));
        return new MyUserDetails(userId, user.getUsername(), user.getPassword(), authorities);
    }
}
