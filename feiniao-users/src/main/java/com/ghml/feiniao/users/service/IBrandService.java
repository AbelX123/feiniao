package com.ghml.feiniao.users.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ghml.feiniao.common.dto.BrandDto;
import com.ghml.feiniao.common.entity.BrandEntity;
import com.ghml.feiniao.common.vo.BrandDetailVo;
import com.ghml.feiniao.common.vo.BrandVo;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description
 */
public interface IBrandService extends IService<BrandEntity> {

    BrandVo login(BrandDto userDto);

    void registerCreators(BrandDto brandDto);

    BrandVo refreshToken(String refreshToken);

    BrandDetailVo getBrandById(String brandId);
}
