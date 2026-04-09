package com.ghml.feiniao.orders.service.impl;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.constants.Gender;
import com.ghml.feiniao.common.dto.OrderDto;
import com.ghml.feiniao.common.entity.OrderRecordEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.OrderRecordMapper;
import com.ghml.feiniao.common.vo.OrderRecordVo;
import com.ghml.feiniao.orders.service.OrderService;
import com.ghml.feiniao.security.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRecordMapper orderRecordMapper;

    @Override
    public String createOrder(OrderDto dto) {

        String currentUserId = SecurityUtils.getCurrentUserId();
        if (dto == null) {
            throw new ServiceException(Code.PARAM_ERROR);
        }

        String orderId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");
        OrderRecordEntity entity = new OrderRecordEntity();
        entity.setOrderId(orderId);
        entity.setProductName(dto.getProductName());
        entity.setProductDetails(dto.getProductDetails());
        entity.setVideoFormat(dto.getVideoFormat());
        entity.setVideoNum(dto.getVideoNum());
        entity.setCreatorId(dto.getCreatorId());
        entity.setBrandId(currentUserId);
        entity.setPlatformCode(dto.getPlatformCode());
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderStatus(0);
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());

        int inserted = orderRecordMapper.insert(entity);
        if (inserted <= 0) {
            throw new ServiceException(Code.OPERATION_FAILED);
        }
        return orderId;
    }

    @Override
    public List<OrderRecordVo> getOrders() {
        String currentUserId = SecurityUtils.getCurrentUserId();
        try {
            List<OrderRecordVo> records = orderRecordMapper.listOrderRecords(currentUserId);
            if (records == null || records.isEmpty()) {
                return Collections.emptyList();
            }
            records.forEach(record -> record.setGender(Gender.getDescByCode(record.getGenderCode())));
            return records;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取订单列表失败: brandId={}, reason={}", currentUserId, e.getMessage(), e);
            throw new ServiceException(Code.OPERATION_FAILED);
        }
    }

    @Override
    public void cancelOrder(String orderId) {
        if (StringUtils.isBlank(orderId)) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        String currentUserId = SecurityUtils.getCurrentUserId();
        int updated = orderRecordMapper.cancelOrder(orderId, currentUserId);
        if (updated <= 0) {
            throw new ServiceException(Code.OPERATION_FAILED);
        }
    }
}
