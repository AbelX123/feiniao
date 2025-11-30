package com.ghml.feiniao.security.config;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.exception.ServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description 认证信息验证
 */
@Data
@Slf4j
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    public MyAuthenticationProvider(@Qualifier("inMybatisUserDetailsService")
                                    UserDetailsService userDetailsService,
                                    PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // 从数据库中取出密码进行比较
        UserDetails userDetails = userDetailsService.loadUserByUsername(authentication.getPrincipal().toString());
        String password = authentication.getCredentials().toString();
        boolean matches = passwordEncoder.matches(password, userDetails.getPassword());
        if (!matches) {
            log.info("[{}]密码验证失败!", userDetails.getUsername());
            throw new ServiceException(Code.USER_PASSWORD_NOT_MATCH);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, password, userDetails.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
