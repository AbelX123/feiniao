package com.ghml.feiniao.users.service.impl;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.RedisPrefix;
import com.ghml.feiniao.common.dto.CaptchaVerifyDto;
import com.ghml.feiniao.common.entity.SmsSendLogEntity;
import com.ghml.feiniao.common.mapper.SmsSendLogMapper;
import com.ghml.feiniao.common.service.RedisService;
import com.ghml.feiniao.common.utils.PhoneUtils;
import com.ghml.feiniao.users.config.AliyunSmsProps;
import com.ghml.feiniao.users.service.CaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private static final int DAILY_SEND_LIMIT = 3;
    private static final long CAPTCHA_EXPIRE_MILLIS = 5 * 60 * 1000L; // 5分钟

    private final RedisService redisService;
    private final SmsSendLogMapper smsSendLogMapper;
    private final AliyunSmsProps aliyunSmsProps;

    @Override
    public boolean create(String phoneRaw) {
        String mobile = PhoneUtils.normalizePhone(phoneRaw);
        if (StringUtils.isBlank(mobile)) {
            return false;
        }
        String dailyCountKey = dailyCountKey(mobile);
        Long dailyCount = redisService.increment(dailyCountKey);
        if (dailyCount == null) {
            return false;
        }
        if (dailyCount == 1L) {
            long ttlMillis = millisUntilEndOfDay();
            redisService.expireMillis(dailyCountKey, ttlMillis);
        }
        if (dailyCount > DAILY_SEND_LIMIT) {
            saveSmsLog(buildLog(mobile, 0, "LIMIT", Code.VERIFIED_CODE_DAILY_LIMIT.getMsg(), null, null));
            return false;
        }

        String captcha = String.format("%06d", ThreadLocalRandom.current().nextInt(1_000_000));
        try {
            Client client = new Client(new Config()
                    .setEndpoint(aliyunSmsProps.getEndpoint())
                    .setAccessKeyId(aliyunSmsProps.getAccessKeyId())
                    .setAccessKeySecret(aliyunSmsProps.getAccessKeySecret()));

            SendSmsRequest request = new SendSmsRequest()
                    .setPhoneNumbers(mobile)
                    .setSignName(aliyunSmsProps.getSignName())
                    .setTemplateCode(aliyunSmsProps.getTemplateCode())
                    .setTemplateParam("{\"code\":\"" + captcha + "\"}");

            SendSmsResponse response = client.sendSms(request);
            String respCode = response.getBody() == null ? null : response.getBody().getCode();
            if (!StringUtils.equals("OK", respCode)) {
                String message = response.getBody() == null ? "unknown" : response.getBody().getMessage();
                String requestId = response.getBody() == null ? null : response.getBody().getRequestId();
                String bizId = response.getBody() == null ? null : response.getBody().getBizId();
                saveSmsLog(buildLog(mobile, 0, respCode, message, requestId, bizId));
                log.warn("短信发送失败: phone={}, code={}, message={}", mobile, respCode, message);
                if (Boolean.TRUE.equals(aliyunSmsProps.getFallbackEnabled())) {
                    log.warn("短信降级模式已开启，本次发送按成功处理: phone={}", mobile);
                    log.info("测试验证码(仅调试使用): phone={}, code={}", mobile, captcha);
                    redisService.setExpMillis(RedisPrefix.PREFIX_PHONE_VERIFIED_CODE + mobile, captcha, CAPTCHA_EXPIRE_MILLIS);
                    return true;
                }
                return false;
            }

            String requestId = response.getBody() == null ? null : response.getBody().getRequestId();
            String bizId = response.getBody() == null ? null : response.getBody().getBizId();
            saveSmsLog(buildLog(mobile, 1, respCode, "OK", requestId, bizId));
            redisService.setExpMillis(RedisPrefix.PREFIX_PHONE_VERIFIED_CODE + mobile, captcha, CAPTCHA_EXPIRE_MILLIS);
            return true;
        } catch (Exception e) {
            saveSmsLog(buildLog(mobile, 0, "EXCEPTION", e.getMessage(), null, null));
            if (Boolean.TRUE.equals(aliyunSmsProps.getFallbackEnabled())) {
                log.warn("短信发送异常，启用降级成功: phone={}, reason={}", mobile, e.getMessage());
                log.info("测试验证码(仅调试使用): phone={}, code={}", mobile, captcha);
                redisService.setExpMillis(RedisPrefix.PREFIX_PHONE_VERIFIED_CODE + mobile, captcha, CAPTCHA_EXPIRE_MILLIS);
                return true;
            }
            redisService.decrement(dailyCountKey);
            log.error("短信验证码发送异常: phone={}, reason={}", mobile, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean verify(CaptchaVerifyDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getPhone()) || StringUtils.isBlank(dto.getCaptcha())) {
            return false;
        }

        String phone = PhoneUtils.normalizePhone(dto.getPhone());
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        String key = RedisPrefix.PREFIX_PHONE_VERIFIED_CODE + phone.trim();
        String codeInCache = (String) redisService.get(key);
        if (StringUtils.isBlank(codeInCache)) {
            return false;
        }
        if (!StringUtils.equals(codeInCache, dto.getCaptcha().trim())) {
            return false;
        }

        redisService.delete(key);
        return true;
    }

    private String dailyCountKey(String mobile) {
        String date = LocalDate.now().toString().replace("-", "");
        return RedisPrefix.PREFIX_PHONE_VERIFIED_DAILY_COUNT + date + ":" + mobile;
    }

    private long millisUntilEndOfDay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrowStart = now.toLocalDate().plusDays(1).atStartOfDay();
        return Math.max(Duration.between(now, tomorrowStart).toMillis(), 1000L);
    }

    private SmsSendLogEntity buildLog(String phone,
                                      Integer status,
                                      String providerCode,
                                      String providerMessage,
                                      String requestId,
                                      String bizId) {
        SmsSendLogEntity logEntity = new SmsSendLogEntity();
        logEntity.setPhone(phone);
        logEntity.setBizType("captcha");
        logEntity.setTemplateCode(aliyunSmsProps.getTemplateCode());
        logEntity.setSignName(aliyunSmsProps.getSignName());
        logEntity.setProvider("aliyun");
        logEntity.setProviderCode(providerCode);
        logEntity.setProviderMessage(providerMessage);
        logEntity.setRequestId(requestId);
        logEntity.setBizId(bizId);
        logEntity.setStatus(status);
        logEntity.setCreatedAt(LocalDateTime.now());
        return logEntity;
    }

    private void saveSmsLog(SmsSendLogEntity logEntity) {
        try {
            smsSendLogMapper.insert(logEntity);
        } catch (Exception e) {
            log.warn("短信日志写库失败: phone={}, reason={}", logEntity.getPhone(), e.getMessage());
        }
    }
}
