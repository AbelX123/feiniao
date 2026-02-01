package com.ghml.feiniao.orders.controller;

import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.dto.OrderDto;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.orders.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrdersController {

    private final OrderService orderService;

    @PostMapping
    public R<String> createOrder(@RequestBody OrderDto order) {
        try {
            String orderId = orderService.createOrder(order);
            return R.ok(orderId);
        } catch (ServiceException e) {
            return R.failed(e.getCode());
        }
    }
}
