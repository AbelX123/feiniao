package com.ghml.feiniao.common.config;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-03 15:40
 * @description 全局请求参数去空格
 */
@ControllerAdvice
public class TrimParamControllerAdvice {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
    }
}
