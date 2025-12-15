package com.ghml.feiniao.users.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.constants.HttpHeaders;
import com.ghml.feiniao.common.dto.UserDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.vo.UserVo;
import com.ghml.feiniao.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 10:41
 * @description
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 注册公共接口
    @PostMapping("/signUp")
    public R<?> register(@Valid @RequestBody UserDto userDto) {
        try {
            userService.signUp(userDto);
            return R.ok();
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 登录公共接口
    @PostMapping("/signIn")
    public R<UserVo> login(@RequestBody UserDto userDto) {
        try {
            UserVo vo = userService.signIn(userDto);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 刷新token公共接口
    @PostMapping("/refresh-token")
    public R<UserVo> refresh(@RequestHeader(HttpHeaders.REFRESH_TOKEN) String refreshToken) {
        try {
            UserVo vo = userService.refreshToken(refreshToken);
            return R.ok(vo);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    // 退出登录
    @DeleteMapping("/signOut")
    public R<?> signOut() {
        try {
            userService.signOut();
            return R.ok();
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

}
