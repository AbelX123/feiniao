package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.constants.MemberLevel;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.dto.BrandDto;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.BrandMapper;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.utils.JwtUtils;
import com.ghml.feiniao.common.vo.BrandDetailVo;
import com.ghml.feiniao.common.vo.BrandVo;
import com.ghml.feiniao.security.config.MyUserDetails;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.config.MinIOConfig;
import com.ghml.feiniao.users.service.IBrandService;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.jsonwebtoken.Claims;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;
import java.util.UUID;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Slf4j
@Service
public class BrandServiceImpl extends ServiceImpl<BrandMapper, BrandEntity> implements IBrandService {

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RedisService redisService;
    private final MinioClient minioClient;
    private final MinIOConfig minIOConfig;

    public BrandServiceImpl(PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager,
                            RedisService redisService,
                            MinioClient minioClient,
                            MinIOConfig minIOConfig) {
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.redisService = redisService;
        this.minioClient = minioClient;
        this.minIOConfig = minIOConfig;
    }


    // 产品主入驻
    @Override
    public void registerCreators(BrandDto brandDto) {
        //  唯一性校验
        LambdaQueryWrapper<BrandEntity> query = new LambdaQueryWrapper<>();
        query.eq(BrandEntity::getUsername, brandDto.getUsername());
        if (this.getOneOpt(query).isPresent()) {
            throw new ServiceException(Code.USER_EXIST);
        }
        BrandEntity entity = new BrandEntity();
        entity.setUserId(StringUtils.replace(UUID.randomUUID().toString(), "-", ""));
        entity.setUsername(brandDto.getUsername());
        entity.setPassword(passwordEncoder.encode(brandDto.getPassword()));
        this.save(entity);
    }

    //  刷新令牌
    @Override
    public BrandVo refreshToken(String refreshToken) {
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

        BrandVo vo = new BrandVo();
        vo.setAccess_token(newAccessToken);
        vo.setRefresh_token(newRefreshToken);

        return vo;
    }

    // 根据编号查询产品主信息
    @Override
    public BrandDetailVo getBrandById() {
        Optional<BrandEntity> opt = this.getOptById(SecurityUtils.getCurrentUserId());
        if (opt.isEmpty()) {
            throw new ServiceException(Code.USER_NOT_EXIST);
        }
        BrandEntity entity = opt.get();
        return BrandDetailVo.builder()
                .userId(SecurityUtils.getCurrentUserId())
                .username(entity.getUsername())
                .phone(entity.getPhone())
                .avatar(entity.getAvatar())
                .memberLeve(MemberLevel.getNameByCode(entity.getMemberLevel()))
                .build();
    }

    // 上传头像到OSS
    @Override
    public String uploadAvatar(MultipartFile file) {
        String currentUserId = SecurityUtils.getCurrentUserId();
        String filename = currentUserId + "." + StringUtils.substringAfter(file.getOriginalFilename(), ".");
        try {
            return MinIOUtils.uploadFile(minioClient, file, Bucket.AVATARS.getName(), filename);
        } catch (Exception e) {
            log.error("OSS文件上传失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    // 获取头像外链
    @Override
    public String getAvatarUrl(String filename) {
        // 校验filename和用户是否一致
        String currentUserId = SecurityUtils.getCurrentUserId();
        if (!StringUtils.startsWith(filename, currentUserId)) {
            throw new ServiceException(Code.OSS_NOT_EXIST);
        }
        try {
            return MinIOUtils.getObjectUrl(minioClient, Bucket.AVATARS.getName(), filename, 30);
        } catch (Exception e) {
            log.error("OSS外链获取失败:{}", e.getMessage());
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    // 登录业务
    @Override
    public BrandVo login(BrandDto brandDto) {
        // 密码验证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(brandDto.getUsername(), brandDto.getPassword());
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        // 认证未通过
        if (!authenticate.isAuthenticated()) {
            throw new ServiceException(Code.USER_PASSWORD_NOT_MATCH);
        }
        // 认证通过
        MyUserDetails myUserDetails = (MyUserDetails) authenticate.getPrincipal();
        String accessToken = JwtUtils.generateToken(myUserDetails.getUserId());
        String refreshToken = JwtUtils.generateRefreshToken(myUserDetails.getUserId());
        BrandVo vo = new BrandVo();
        vo.setAccess_token(accessToken);
        vo.setRefresh_token(refreshToken);

        // 缓存access_token, refresh_token
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_TOKEN + myUserDetails.getUserId(), accessToken, JwtUtils.getExpiration(accessToken));
        redisService.setExpMillis(RedisPrefix.PREFIX_WEB_REFRESH_TOKEN + myUserDetails.getUserId(), refreshToken, JwtUtils.getExpiration(refreshToken));

        return vo;
    }
}
