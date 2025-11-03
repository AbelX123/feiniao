package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghml.feiniao.common.dto.CreatorDto;
import com.ghml.feiniao.common.entity.CreatorEntity;
import com.ghml.feiniao.common.vo.CreatorDetailVo;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
@Mapper
public interface CreatorMapper extends BaseMapper<CreatorEntity> {

    // 多条件分页查询创作者
    Page<CreatorEntity> selectCreatorsByConditions(@Param("page") Page<CreatorEntity> page,
                                                   @Param("query") CreatorDto query);

    // 依据用户编号查询用户信息，country_code->country_name
    @Select("SELECT cp.user_id,  " +
            "       cp.username,  " +
            "       cp.country_code,  " +
            "       cp.gender,  " +
            "       c.country_name  " +
            "FROM creator_profiles cp  " +
            "         LEFT JOIN country c ON cp.country_code = c.country_code  " +
            "WHERE cp.user_id = #{creatorId}")
    Optional<CreatorEntity> getOptByCreatorId(String creatorId);

    // 根据产品主编号获取收藏的创作者列表
    @Select("SELECT bcm.brand_id, cp.username, cp.video_price, c.country_name, cp.gender, ar.age_range_desc " +
            "FROM brand_favorite_creators bcm " +
            "         INNER JOIN creator_profiles cp ON bcm.creator_id = cp.user_id " +
            "         LEFT JOIN country c ON c.country_code = cp.country_code " +
            "         LEFT JOIN age_range ar ON ar.age_range = cp.age_range " +
            "WHERE bcm.brand_id = #{brandId} " +
            "ORDER BY bcm.brand_id")
    Page<CreatorEntity> favoriteCreators(Page<CreatorEntity> page, String brandId);

    // 依据用户编号查询模特类别
    @Select("SELECT mt.model_type_name " +
            "FROM creator_type_mapping ctm " +
            "         INNER JOIN model_type mt ON ctm.model_type_id = mt.model_type_id " +
            "WHERE ctm.creator_id = #{creatorId}")
    List<String> getModelTypesById(String creatorId);

    // 依据用户编号查询拍摄平台
    @Select("SELECT p.platform_name " +
            "FROM creator_platform_mapping cpm " +
            "         INNER JOIN platform p ON cpm.platform_code = p.platform_code " +
            "WHERE cpm.creator_id = #{creatorId}")
    List<String> getPlatforms(String creatorId);

    // 依据用户编号查询擅长品类
    @Select("SELECT s.specialty_name " +
            "FROM creator_specialty_mapping csm " +
            "         INNER JOIN specialty s ON csm.specialty_id = s.specialty_id " +
            "WHERE csm.creator_id = #{creatorId}")
    List<String> getSpecialties(String creatorId);

    // 依据用户编号查询模特标签
    @Select("SELECT tag_name " +
            "FROM creator_tag_mapping ctm " +
            "         INNER JOIN model_tag mt ON ctm.tag_id = mt.tag_id " +
            "where ctm.creator_id = #{creatorId}")
    List<String> getTags(String creatorId);

    // 依据用户编号查询模特案例
    @Select("SELECT case_id, case_title, cover_url " +
            "FROM creator_case " +
            "WHERE creator_id = #{creatorId} " +
            "  AND status = 1")
    List<CreatorDetailVo.CaseVo> getCaseVos(String creatorId);

}
