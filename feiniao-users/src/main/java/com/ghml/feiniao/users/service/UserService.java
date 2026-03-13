package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.dto.SignInByPhoneDto;
import com.ghml.feiniao.common.dto.UserDto;
import com.ghml.feiniao.common.vo.UserVo;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 10:58
 * @description
 */
public interface UserService {

    void signUp(UserDto userDto);

    UserVo signIn(UserDto userDto);

    /**
     * 手机号验证码登录/注册一体化：验证通过后若已注册则登录，未注册则创建用户并登录。
     */
    UserVo signInByPhone(SignInByPhoneDto dto);

    UserVo refreshToken(String refreshToken);

    void signOut();
}
