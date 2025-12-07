package com.ghml.feiniao.users.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.RoleMenuEntity;
import com.ghml.feiniao.common.mapper.RoleMenuMapper;
import com.ghml.feiniao.users.service.RoleMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenuEntity> implements RoleMenuService {
}
