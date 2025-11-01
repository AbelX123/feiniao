package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorDetailVo;
import com.ghml.feiniao.common.vo.CreatorVo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
public interface CreatorService extends IService<CreatorEntity> {
    PageResult<CreatorVo> selectCreatorsByConditions(CreatorDto creatorDto);

    CreatorDetailVo getCreatorById(String creatorId);

    void followCreator(String creatorId);

    void unfollowCreator(String creatorId);

    PageResult<CreatorVo> favoriteCreators(CreatorDto creatorDto);

    String uploadVideo(MultipartFile video);
}
