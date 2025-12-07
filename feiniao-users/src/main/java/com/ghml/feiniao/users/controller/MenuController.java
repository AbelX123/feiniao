package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.vo.MenuVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单接口
 */
@RestController
@RequestMapping("/api/menus")
@RequiredArgsConstructor
public class MenuController {

    private final MenuService menuService;

    @GetMapping
    public R<List<MenuVo>> getMenus(@RequestParam Integer roleId) {
        SecurityUtils.getCurrentUserId();
        try {
            List<MenuVo> vos = menuService.getMenus(roleId);
            return R.ok(vos);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }
}
