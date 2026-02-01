package com.ghml.feiniao.orders.service.impl;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.dto.OrderDto;
import com.ghml.feiniao.common.entity.OrderRecordEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.OrderRecordMapper;
import com.ghml.feiniao.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRecordMapper orderRecordMapper;

    @Override
    public String createOrder(OrderDto dto) {
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
        entity.setPlatformCode(dto.getPlatformCode());
        entity.setOrderAmount(dto.getOrderAmount());
        entity.setOrderStatus(0);
        entity.setCreateTime(LocalDateTime.now());

        int inserted = orderRecordMapper.insert(entity);
        if (inserted <= 0) {
            throw new ServiceException(Code.OPERATION_FAILED);
        }
        return orderId;
    }
}
