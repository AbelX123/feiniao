package com.ghml.feiniao.orders.service;

import com.ghml.feiniao.common.dto.OrderDto;
import com.ghml.feiniao.common.vo.OrderRecordVo;

import java.util.List;

public interface OrderService {

    String createOrder(OrderDto dto);

    List<OrderRecordVo> getOrders();
}
