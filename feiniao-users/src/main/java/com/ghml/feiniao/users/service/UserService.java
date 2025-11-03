package com.ghml.feiniao.users.service;

import com.ghml.feiniao.common.dto.UserDto;
import com.ghml.feiniao.common.vo.UserVo;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-02 10:58
 * @description
 */
public interface UserService {

    String register(UserDto userDto);

    UserVo login(UserDto userDto);

    UserVo refreshToken(String refreshToken);
}
