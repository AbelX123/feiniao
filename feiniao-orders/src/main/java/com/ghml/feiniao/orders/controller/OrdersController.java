package com.ghml.feiniao.orders.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.OrderDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.vo.OrderRecordVo;
import com.ghml.feiniao.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    /**
     * 创建订单
     */
    @PostMapping
    public R<String> createOrder(@RequestBody OrderDto order) {
        try {
            String orderId = orderService.createOrder(order);
            return R.ok(orderId);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }

    /**
     * 获取订单列表
     */
    @GetMapping
    public R<List<OrderRecordVo>> getOrders() {
        try {
            List<OrderRecordVo> records = orderService.getOrders();
            return R.ok(records);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }
}
