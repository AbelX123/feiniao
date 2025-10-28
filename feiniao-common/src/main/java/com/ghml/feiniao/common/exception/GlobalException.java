package com.ghml.feiniao.common.exception;

import com.ghml.feiniao.common.api.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 全局异常
 */
@RestControllerAdvice
public class GlobalException {

    // 处理自定义异常
    @ExceptionHandler(value = ServiceException.class)
    public R<?> serviceException(ServiceException e) {
        return R.failed(e.getCode());
    }
}
