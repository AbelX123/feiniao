package com.ghml.feiniao.dicts.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.entity.CountryEntity;
import com.ghml.feiniao.common.vo.CountryVo;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:34
 * @description
 */
public interface CountryService extends IService<CountryEntity> {
    List<CountryVo> getCountries();
}
