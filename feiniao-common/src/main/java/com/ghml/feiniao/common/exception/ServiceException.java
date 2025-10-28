package com.ghml.feiniao.common.exception;

import com.ghml.feiniao.common.api.Code;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 自定义异常
 */
@EqualsAndHashCode(callSuper = true)    // 是否调用父类的equals和hashcode方法
@Data
public class ServiceException extends RuntimeException {

    private Code code;

    public ServiceException(Code code) {
        super((code.getMsg()));
        this.code = code;
    }
}
