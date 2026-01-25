package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.utils.PageResult;
import com.ghml.feiniao.common.vo.*;
import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
public interface CreatorService extends IService<CreatorEntity> {

    PageResult<CreatorDisplayVo> selectCreatorsByConditions(CreatorsDto creatorDto);

    CreatorDetailsVo getCreatorById(String creatorId);

    PageResult<CreatorDisplayVo> favoriteCreators(CreatorsDto creatorsDto);

    void register(CreatorEntity creator);

    List<ModelTypeVo> getModelTypesById(String creatorId);

    List<PlatformVo> getPlatforms(String creatorId);

    List<SpecialtyVo> getSpecialties(String creatorId);

    List<TagVo> getTags(String creatorId);

    CreatorDetailsVo patchCreator(CreatorDto dto);
}
