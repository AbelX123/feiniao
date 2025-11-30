package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.UserRolesEntity;
import com.ghml.feiniao.common.mapper.UserRolesMapper;
import com.ghml.feiniao.users.service.UserRolesService;
import org.springframework.stereotype.Service;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-23 16:34
 * @description
 */
@Service
public class UserRolesServiceImpl extends ServiceImpl<UserRolesMapper, UserRolesEntity> implements UserRolesService {
    @Override
    public void saveUserRoles(UserRolesEntity userRolesEntity) {
        this.save(userRolesEntity);
    }
}
