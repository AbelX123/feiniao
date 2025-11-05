package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghml.feiniao.common.entity.PermissionEntity;
import com.ghml.feiniao.common.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 12:45
 * @description
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleEntity> {

    @Select("SELECT rp.permission_id, " +
            "       p.permission_code, " +
            "       p.permission_desc, " +
            "       p.http_method, " +
            "       p.api_path " +
            "FROM role_permissions rp " +
            "         INNER JOIN permissions p ON rp.permission_id = p.permission_id " +
            "WHERE rp.role_id = #{roleId}")
    List<PermissionEntity> selectPermissionsByRoleId(Integer roleId);
}
