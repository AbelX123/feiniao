package com.ghml.feiniao.dicts.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.vo.CountryVo;
import com.ghml.feiniao.dicts.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-29 11:33
 * @description
 */
@RestController
@RequestMapping("/api/dicts")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/countries")
    public R<List<CountryVo>> getCountries() {
        List<CountryVo> vos = countryService.getCountries();
        return R.ok(vos);
    }
}
