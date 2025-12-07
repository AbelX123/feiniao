package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.MenuEntity;
import com.ghml.feiniao.common.vo.MenuVo;

import java.util.List;

public interface MenuService extends IService<MenuEntity> {
    List<MenuVo> getMenus(Integer roleId);
}
