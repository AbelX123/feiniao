package com.ghml.feiniao.dicts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.PlatformEntity;
import com.ghml.feiniao.common.mapper.PlatformMapper;
import com.ghml.feiniao.common.vo.PlatformVo;
import com.ghml.feiniao.dicts.service.IPlatformService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 23:51
 * @description
 */
@Slf4j
@Service
public class PlatformServiceImpl extends ServiceImpl<PlatformMapper, PlatformEntity> implements IPlatformService {

    @Override
    public List<PlatformVo> getPlatforms() {
        List<PlatformEntity> entities = this.list();
        return convertToVoList(entities);
    }

    private List<PlatformVo> convertToVoList(List<PlatformEntity> entities) {
        return entities.stream().map(entity -> PlatformVo.builder()
                .platformCode(entity.getPlatformCode())
                .platformName(entity.getPlatformName())
                .build()
        ).collect(Collectors.toList());
    }
}
