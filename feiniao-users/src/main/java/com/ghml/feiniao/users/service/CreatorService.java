package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.CreatorVo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
public interface CreatorService extends IService<CreatorEntity> {
    PageResult<CreatorVo> selectCreatorsByConditions(CreatorDto creatorDto);

    CreatorVo getCreatorById(String creatorId);

    PageResult<CreatorVo> favoriteCreators(CreatorDto creatorDto);

    String uploadVideo(MultipartFile video);

    void register(CreatorEntity creator);

    List<String> getModelTypesById(String creatorId);

    List<String> getPlatforms(String creatorId);

    List<String> getSpecialties(String creatorId);

    List<String> getTags(String creatorId);

    List<CreatorVo.CaseVo> getCaseVos(String creatorId);
}
