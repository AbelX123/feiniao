package com.ghml.feiniao.common.annos;

import com.ghml.feiniao.common.dto.BrandDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-11-08 09:58
 * @description
 */
public class PhoneNumberGroupValidator implements ConstraintValidator<ValidPhoneNumberGroup, BrandDto> {
    @Override
    public boolean isValid(BrandDto brandDto, ConstraintValidatorContext cvc) {
        if (brandDto == null) {
            return true;
        }
        String phoneCountryCode = brandDto.getPhoneCountryCode();
        String phoneNumber = brandDto.getPhoneNumber();
        String phoneFull = brandDto.getPhoneFull();
        boolean allNull = StringUtils.isEmpty(phoneCountryCode) && StringUtils.isEmpty(phoneNumber) && StringUtils.isEmpty(phoneFull);
        boolean allPresent = !StringUtils.isEmpty(phoneCountryCode) && !StringUtils.isEmpty(phoneNumber) && !StringUtils.isEmpty(phoneFull);

        return allNull || allPresent;
    }
}
