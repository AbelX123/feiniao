package com.ghml.feiniao.common.annos;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-08 09:51
 * @description 自定义手机号校验注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PhoneNumberGroupValidator.class)
public @interface ValidPhoneNumberGroup {
    String message() default "请校验手机号填写";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
