package com.ghml.feiniao.common.exception;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 全局异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalException {

    // 处理自定义异常
    @ExceptionHandler(value = ServiceException.class)
    public R<?> serviceException(ServiceException e) {
        return R.failed(e.getCode());
    }

    // 其他异常
    @ExceptionHandler(value = Exception.class)
    public R<?> exception(Exception e) {
        log.warn("未知异常:{}", e.getMessage());
        e.printStackTrace();
        return R.failed(Code.OPERATION_FAILED);
    }

    // 校验异常
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R<?> bindingResultHandler(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) ->
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage()));
        log.warn("参数校验失败: |{}", JSON.toJSONString(e.getBindingResult().getTarget()));
        return R.failed(Code.PARAM_ERROR, JSONObject.from(errorMap));
    }

    // 路径请求错误
    @ExceptionHandler(value = NoResourceFoundException.class)
    public R<?> noResourceFoundException(NoResourceFoundException e) {
        log.warn("路径错误:{}", e.getMessage());
        return R.failed(Code._404);
    }
}
