package com.ghml.feiniao.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author YUHUAI
 * @version 1.0
 * @date 2025-10-28 21:06
 * @description Jwt工具类
 */
public class JwtUtils {

    // 密钥
    private final static String SECRET_KEY = "MSNEUTSSLIVOGPNXIZFGOJBBWJUVSGRL";

    // token过期时间
    private final static Long TOKEN_EXPIRE_TIME = 12 * 60 * 60 * 1000L;

    // refresh_token过期时间
    private final static Long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L;

    private final static SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    // 抽离生成token
    public static String generate(String subject, Long expire) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))  // 统一使用系统时间戳
                .expiration(new Date(System.currentTimeMillis() + expire))
                .signWith(secretKey)
                .compact();
    }

    // 生成token
    public static String generateToken(String subject) {
        return generate(subject, TOKEN_EXPIRE_TIME);
    }

    // 生成refresh_token
    public static String generateRefreshToken(String subject) {
        return generate(subject, REFRESH_TOKEN_EXPIRE_TIME);
    }


    // 解析token
    public static Claims parseToken(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // 判定是否过期，这里只能返回未过期的false状态，如果过期会抛出异常 ExpiredJwtException
    public static boolean isExpired(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            // 其他异常也视为不可用
            return true;
        }
    }

    // 验证token是否有效（未过期且能正常解析）
    public static boolean isValid(String token) {
        return !isExpired(token);
    }

    // 获取过期时间
    public static Long getExpiration(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().getTime();
        } catch (Exception e) {
            return null;
        }
    }

    // 获取subject
    public static String getSubject(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
