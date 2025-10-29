package com.ghml.feiniao.dicts.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ghml.feiniao.common.entity.CountryEntity;
import com.ghml.feiniao.common.mapper.CountryMapper;
import com.ghml.feiniao.common.vo.CountryVo;
import com.ghml.feiniao.dicts.service.CountryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:36
 * @description
 */
@Service
public class CountryServiceImpl extends ServiceImpl<CountryMapper, CountryEntity> implements CountryService {
    @Override
    public List<CountryVo> getCountries() {
        List<CountryEntity> entities = this.list();
        return convertToVoList(entities);
    }

    // 将Entity列表转换为VO列表
    private List<CountryVo> convertToVoList(List<CountryEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ArrayList<>();
        }
        // 转换每个Entity为VO
        return entities.stream().map(entity -> CountryVo.builder()
                .countryCode(entity.getCountryCode())
                .countryName(entity.getCountryName())
                .build()
        ).collect(Collectors.toList());
    }
}
