package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ghml.feiniao.common.dto.CreatorsDto;
import com.ghml.feiniao.common.entity.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

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
                                                   @Param("query") CreatorsDto query,
                                                   @Param("userId") String userId);

    // 依据用户编号查询用户信息，country_code->country_name
    @Select("SELECT cp.user_id,  " +
            "       cp.username,  " +
            "       cp.video_price,  " +
            "       cp.gender,  " +
            "       ar.age_range_desc,  " +
            "       cp.is_available,  " +
            "       cp.avatar_url,  " +
            "       cp.avatar_url_expiry,  " +
            "       c.country_name  " +
            "FROM creator_profiles cp  " +
            "         LEFT JOIN country c ON cp.country_code = c.country_code  " +
            "         LEFT JOIN age_range ar ON cp.age_range = ar.age_range  " +
            "WHERE cp.user_id = #{creatorId}")
    Optional<CreatorEntity> getOptByCreatorId(String creatorId);

    // 根据产品主编号获取收藏的创作者列表
    @Select("""
            SELECT bfc.creator_id as user_id,
            	cp.username,
            	cp.avatar_url,
            	cp.avatar_url_expiry,
            	cp.gender,
            	cp.video_price,
            	cp.country_code,
            	cp.is_available,
            	ar.age_range_desc,
            	c.country_name,
            	1 AS is_favorite
            FROM
            	brand_favorite_creators bfc
            INNER JOIN creator_profiles cp ON
            	bfc.creator_id = cp.user_id
            LEFT JOIN country c ON
            	c.country_code = cp.country_code
            LEFT JOIN age_range ar ON
            	ar.age_range = cp.age_range
            WHERE
            	bfc.brand_id = #{brandId}
            ORDER BY
            	bfc.brand_id""")
    Page<CreatorEntity> favoriteCreators(Page<CreatorEntity> page, String brandId);

    // 依据用户编号查询模特类别
    @Select("SELECT mt.model_type_id, mt.model_type_name " +
            "FROM creator_type_mapping ctm " +
            "         INNER JOIN model_type mt ON ctm.model_type_id = mt.model_type_id " +
            "WHERE ctm.creator_id = #{creatorId}")
    List<ModelTypeEntity> getModelTypesById(String creatorId);

    // 依据用户编号查询拍摄平台
    @Select("SELECT p.platform_code, p.platform_name " +
            "FROM creator_platform_mapping cpm " +
            "         INNER JOIN platform p ON cpm.platform_code = p.platform_code " +
            "WHERE cpm.creator_id = #{creatorId}")
    List<PlatformEntity> getPlatforms(String creatorId);

    // 依据用户编号查询擅长品类
    @Select("SELECT s.specialty_id, s.specialty_name " +
            "FROM creator_specialty_mapping csm " +
            "         INNER JOIN specialty s ON csm.specialty_id = s.specialty_id " +
            "WHERE csm.creator_id = #{creatorId}")
    List<SpecialtyEntity> getSpecialties(String creatorId);

    // 依据用户编号查询模特标签
    @Select("SELECT mt.tag_id, mt.tag_name " +
            "FROM creator_tag_mapping ctm " +
            "         INNER JOIN model_tag mt ON ctm.tag_id = mt.tag_id " +
            "where ctm.creator_id = #{creatorId}")
    List<TagEntity> getTags(String creatorId);

    // 依据用户编号查询模特案例（含 creator_id 供刷新 MinIO 外链时拼接 objectKey）
    @Select("SELECT case_id, creator_id, case_title, cover_url, cover_url_expiry, video_url, video_url_expiry, status, create_time " +
            "FROM cases " +
            "WHERE creator_id = #{creatorId} " +
            "  AND status = 1")
    List<CaseEntity> getCaseVos(String creatorId);

}
