package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.MenuEntity;
import com.ghml.feiniao.common.mapper.MenuMapper;
import com.ghml.feiniao.common.vo.MenuVo;
import com.ghml.feiniao.users.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl extends ServiceImpl<MenuMapper, MenuEntity> implements MenuService {

    private final MenuMapper menuMapper;

    @Override
    public List<MenuVo> getMenus(Integer roleId) {
        // 获取角色菜单列表
        List<MenuEntity> menus = menuMapper.getMenusByRoleId(roleId);

        // 构建MenuVo列表
        return menus.stream()
                .map(menu ->
                        MenuVo.builder()
                                .key(menu.getMenuKey())
                                .label(menu.getMenuLabel())
                                .icon(menu.getIcon())
                                .path(menu.getPath())
                                .order(menu.getSortOrder())
                                .build()
                ).sorted(Comparator.comparing(MenuVo::getOrder))
                .toList();
    }
}
