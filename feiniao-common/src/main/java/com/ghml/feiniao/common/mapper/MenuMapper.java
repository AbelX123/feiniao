package com.ghml.feiniao.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ghml.feiniao.common.entity.MenuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MenuMapper extends BaseMapper<MenuEntity> {

    @Select("SELECT m.menu_id, m.menu_key, m.menu_label, m.icon, m.path, m.sort_order " +
            "FROM menus m " +
            "         INNER JOIN role_menus rm ON m.menu_id = rm.menu_id " +
            "WHERE rm.role_id = #{roleId} " +
            "  and m.is_visible = 1")
    List<MenuEntity> getMenusByRoleId(@Param("roleId") Integer roleId);
}
