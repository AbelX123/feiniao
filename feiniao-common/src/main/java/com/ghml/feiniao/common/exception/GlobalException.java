package com.ghml.feiniao.common.exception;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.api.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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
        log.error("未知异常:{}", e.getMessage());
        return R.failed(Code.OPERATION_FAILED);
    }
}
