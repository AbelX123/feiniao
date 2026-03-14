package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.PhoneStatus;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.constants.Role;
import com.ghml.feiniao.common.dto.CaptchaVerifyDto;
import com.ghml.feiniao.common.dto.SignInByPhoneDto;
import com.ghml.feiniao.common.dto.UpdatePasswordDto;
import com.ghml.feiniao.common.dto.UserDto;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.entity.UserEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.UserMapper;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.utils.JwtUtils;
import com.ghml.feiniao.common.utils.PhoneUtils;
import com.ghml.feiniao.common.vo.UserVo;
import com.ghml.feiniao.security.config.MyUserDetails;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.service.CaptchaService;
import com.ghml.feiniao.users.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 11:06
 * @description
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final TransactionTemplate transactionTemplate;
    private final CreatorService creatorService;
    private final BrandService brandService;
    private final RoleService roleService;
    private final CaptchaService captchaService;

    private static final String DEFAULT_PASSWORD = "123456";

    // 公共注册
    @Override
    public void signUp(UserDto userDto) {
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
                log.warn("用户[{}]注册异常: {} 已回滚!", userDto.getUsername(), e.getMessage());
                throw new ServiceException(Code.OPERATION_FAILED);
            }
        });
        log.info("用户[{}]注册成功!", userDto.getUsername());
    }

    // 公共登录
    @Override
    public UserVo signIn(UserDto userDto) {
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
        vo.setUserId(myUserDetails.getUserId());
        vo.setUsername(myUserDetails.getUsername());
        vo.setRoleId(myUserDetails.getRoleId());
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);

        // 缓存access_token, refresh_token
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_TOKEN + myUserDetails.getUserId(), accessToken, JwtUtils.getExpiration(accessToken));
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + myUserDetails.getUserId(), refreshToken, JwtUtils.getExpiration(refreshToken));

        log.info("用户[{}]登录成功!", userDto.getUsername());

        return vo;
    }

    // 手机号验证码登录/注册
    @Override
    public UserVo signInByPhone(SignInByPhoneDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getPhoneNumber()) || StringUtils.isBlank(dto.getCaptcha())) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        String phoneNumber = PhoneUtils.normalizePhone(dto.getPhoneNumber());
        if (StringUtils.isBlank(phoneNumber)) {
            throw new ServiceException(Code.PHONE_NOT_RIGHT);
        }
        // 验证验证码（会删除 Redis 中的验证码）
        CaptchaVerifyDto verifyDto = new CaptchaVerifyDto();
        verifyDto.setPhone(phoneNumber);
        verifyDto.setCaptcha(dto.getCaptcha());
        if (!captchaService.verify(verifyDto)) {
            throw new ServiceException(Code.VERIFIED_CODE_FAILED);
        }

        // 按手机号查是否已注册（创作者或产品主）
        String existingUserId = findUserIdByPhoneNumber(phoneNumber);
        if (existingUserId != null) {
            return issueTokenAndBuildVo(existingUserId);
        }

        // 未注册：必须传角色
        if (dto.getRoleId() == null || (!Role.BRAND.getRoleId().equals(dto.getRoleId()) && !Role.CREATOR.getRoleId().equals(dto.getRoleId()))) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        if (roleService.getOptById(dto.getRoleId()).isEmpty()) {
            throw new ServiceException(Code.PARAM_ERROR);
        }

        String userId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        String username = "u_" + userId.substring(0, 8);
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        userEntity.setUsername(username);
        userEntity.setPassword(passwordEncoder.encode(DEFAULT_PASSWORD));
        userEntity.setRoleId(dto.getRoleId());

        transactionTemplate.executeWithoutResult(status -> {
            try {
                this.save(userEntity);
                if (Role.BRAND.getRoleId().equals(dto.getRoleId())) {
                    BrandEntity brand = new BrandEntity();
                    brand.setUserId(userId);
                    brand.setUsername(username);
                    brand.setPhoneNumber(phoneNumber);
                    brand.setPhoneVerified(PhoneStatus.VERIFIED.getCode());
                    brand.setVerifiedAt(LocalDateTime.now());
                    brandService.register(brand);
                } else {
                    CreatorEntity creator = new CreatorEntity();
                    creator.setUserId(userId);
                    creator.setUsername(username);
                    creator.setPhoneNumber(phoneNumber);
                    creator.setPhoneVerified(PhoneStatus.VERIFIED.getCode());
                    creator.setVerifiedAt(new java.util.Date());
                    creatorService.register(creator);
                }
            } catch (Exception e) {
                status.setRollbackOnly();
                log.warn("手机号[{}]注册异常: {} 已回滚!", phoneNumber, e.getMessage());
                throw new ServiceException(Code.OPERATION_FAILED);
            }
        });

        log.info("手机号[{}]注册并登录成功, roleId={}", phoneNumber, dto.getRoleId());
        return issueTokenAndBuildVo(userId);
    }

    private String findUserIdByPhoneNumber(String phoneNumber) {
        CreatorEntity creator = creatorService.getOne(new LambdaQueryWrapper<CreatorEntity>().eq(CreatorEntity::getPhoneNumber, phoneNumber));
        if (creator != null) {
            return creator.getUserId();
        }
        BrandEntity brand = brandService.getOne(new LambdaQueryWrapper<BrandEntity>().eq(BrandEntity::getPhoneNumber, phoneNumber));
        if (brand != null) {
            return brand.getUserId();
        }
        return null;
    }

    private UserVo issueTokenAndBuildVo(String userId) {
        UserEntity user = this.getById(userId);
        if (user == null) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        String accessToken = JwtUtils.generateToken(userId);
        String refreshToken = JwtUtils.generateRefreshToken(userId);
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_TOKEN + userId, accessToken, JwtUtils.getExpiration(accessToken));
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + userId, refreshToken, JwtUtils.getExpiration(refreshToken));
        UserVo vo = new UserVo();
        vo.setUserId(user.getUserId());
        vo.setUsername(user.getUsername());
        vo.setRoleId(user.getRoleId());
        vo.setAccessToken(accessToken);
        vo.setRefreshToken(refreshToken);
        return vo;
    }

    //  刷新令牌
    @Override
    public UserVo refreshToken(String refreshToken) {
        // 验证token是否
        String userId = SecurityUtils.getCurrentUserId();
        log.info("用户[{}]请求更新令牌!", userId);
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
        log.info("用户[{}]请求更新令牌成功!", userId);

        return vo;
    }

    // 退出登录
    @Override
    public void signOut() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        redisService.delete(RedisPrefix.PREFIX_WEB_TOKEN + currentUserId);
        redisService.delete(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + currentUserId);
        log.info("用户[{}]退出登录!", SecurityUtils.getCurrentUsername());
    }

    // 通过手机号+验证码修改密码
    @Override
    public UserVo updatePassword(UpdatePasswordDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getPhoneNumber()) || StringUtils.isBlank(dto.getCaptcha())
                || StringUtils.isBlank(dto.getPassword()) || StringUtils.isBlank(dto.getConfirmPassword())) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        String phoneNumber = PhoneUtils.normalizePhone(dto.getPhoneNumber());
        if (StringUtils.isBlank(phoneNumber)) {
            throw new ServiceException(Code.PHONE_NOT_RIGHT);
        }
        CaptchaVerifyDto verifyDto = new CaptchaVerifyDto();
        verifyDto.setPhone(phoneNumber);
        verifyDto.setCaptcha(dto.getCaptcha());
        if (!captchaService.verify(verifyDto)) {
            throw new ServiceException(Code.VERIFIED_CODE_FAILED);
        }
        String userId = findUserIdByPhoneNumber(phoneNumber);
        if (userId == null) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        UserEntity user = this.getById(userId);
        if (user == null) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        this.updateById(user);
        log.info("用户[{}]通过手机号修改密码成功", userId);
        return issueTokenAndBuildVo(userId);
    }
}
