package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.vo.BrandDetailVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
public interface BrandService extends IService<BrandEntity> {

    BrandDetailVo getBrandById();

    void followCreator(String creatorId);

    void unfollowCreator(String creatorId);

    String uploadAvatar(MultipartFile file);

    String getAvatarUrl(String filename);

    void register(BrandEntity brandEntity);
}
