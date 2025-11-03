package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.constants.Role;
import com.ghml.feiniao.common.dto.UserDto;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.entity.UserEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.UserMapper;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.utils.JwtUtils;
import com.ghml.feiniao.common.vo.UserVo;
import com.ghml.feiniao.security.config.MyUserDetails;
import com.ghml.feiniao.users.service.BrandService;
import com.ghml.feiniao.users.service.CreatorService;
import com.ghml.feiniao.users.service.RoleService;
import com.ghml.feiniao.users.service.UserService;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 11:06
 * @description
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final TransactionTemplate transactionTemplate;

    private CreatorService creatorService;
    private BrandService brandService;
    private RoleService roleService;

    @Autowired
    public void setBrandService(BrandService brandService) {
        this.brandService = brandService;
    }

    @Autowired
    public void setCreatorService(CreatorService creatorService) {
        this.creatorService = creatorService;
    }

    @Autowired
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public UserServiceImpl(PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           RedisService redisService,
                           TransactionTemplate transactionTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.redisService = redisService;
        this.transactionTemplate = transactionTemplate;
    }

    // 公共注册
    @Override
    public String register(UserDto userDto) {
        // 角色校验
        if (roleService.getOptById(userDto.getRoleId()).isEmpty()) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        // 唯一性校验
        LambdaQueryWrapper<UserEntity> query = new LambdaQueryWrapper<>();
        query.eq(UserEntity::getUsername, userDto.getUsername());
        if (this.getOneOpt(query).isPresent()) {
            throw new ServiceException(Code.USER_EXIST);
        }
        String userId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        UserEntity entity = new UserEntity();
        entity.setUserId(userId);
        entity.setUsername(userDto.getUsername());
        entity.setPassword(passwordEncoder.encode(userDto.getPassword()));
        entity.setRoleId(userDto.getRoleId());
        try {
            transactionTemplate.executeWithoutResult(status -> {
                try {
                    // 保存用户实体
                    UserServiceImpl.this.save(entity);
                    // 产品主注册
                    if (userDto.getRoleId().equals(Role.BRAND.getRoleId())) {
                        BrandEntity brand = new BrandEntity();
                        brand.setUserId(userId);
                        brand.setUsername(userDto.getUsername());
                        brandService.register(brand);
                    }
                    // 创作者注册
                    else if (userDto.getRoleId().equals(Role.CREATOR.getRoleId())) {
                        CreatorEntity creator = new CreatorEntity();
                        creator.setUserId(userId);
                        creator.setUsername(userDto.getUsername());
                        creatorService.register(creator);
                    } else {
                        throw new ServiceException(Code.PARAM_ERROR);
                    }
                } catch (Exception e) {
                    status.setRollbackOnly();
                    e.printStackTrace();
                    throw new ServiceException(Code.OPERATION_FAILED);
                }
            });
            return userId;
        } catch (TransactionException e) {
            throw new RuntimeException(e);
        }
    }

    // 公共登录
    @Override
    public UserVo login(UserDto userDto) {
        // 密码验证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 认证未通过
        if (!authenticate.isAuthenticated()) {
            throw new ServiceException(Code.USER_PASSWORD_NOT_MATCH);
        }
        // 认证通过
        MyUserDetails myUserDetails = (MyUserDetails) authenticate.getPrincipal();
        String accessToken = JwtUtils.generateToken(myUserDetails.getUserId());
        String refreshToken = JwtUtils.generateRefreshToken(myUserDetails.getUserId());
        UserVo vo = new UserVo();
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);

        // 缓存access_token, refresh_token
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_TOKEN + myUserDetails.getUserId(), accessToken, JwtUtils.getExpiration(accessToken));
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + myUserDetails.getUserId(), refreshToken, JwtUtils.getExpiration(refreshToken));

        return vo;
    }

    //  刷新令牌
    @Override
    public UserVo refreshToken(String refreshToken) {
        // 验证token是否
        String userId;
        try {
            Claims claims = JwtUtils.parseToken(refreshToken);
            userId = claims.getSubject();
        } catch (Exception e) {
            log.warn("refresh-token解析失败");
            throw new ServiceException(Code.TOKEN_INVALID);
        }
        // 缓存验证
        String refreshTokenInCache = (String) redisService.get(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + userId);
        if (!StringUtils.equals(refreshToken, refreshTokenInCache)) {
            throw new ServiceException(Code.TOKEN_INVALID);
        }
        // 生成新的token对
        String newAccessToken = JwtUtils.generateToken(userId);
        String newRefreshToken = JwtUtils.generateRefreshToken(userId);

        // 缓存
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_TOKEN + userId, newAccessToken, JwtUtils.getExpiration(newAccessToken));
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + userId, newRefreshToken, JwtUtils.getExpiration(newRefreshToken));

        UserVo vo = new UserVo();
        vo.setAccessToken(newAccessToken);
        vo.setRefreshToken(newRefreshToken);

        return vo;
    }
}
