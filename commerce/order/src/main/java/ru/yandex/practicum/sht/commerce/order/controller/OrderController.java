package ru.yandex.practicum.sht.commerce.order.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.sht.commerce.ia.controller.OrderOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.sht.commerce.ia.dto.order.OrderDto;
import ru.yandex.practicum.sht.commerce.ia.dto.order.ProductReturnRequest;
import ru.yandex.practicum.sht.commerce.order.service.OrderService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class OrderController implements OrderOperations {

    private final OrderService orderService;

    public List<OrderDto> getOrder(@NotNull String username, Pageable pageable) {
        return orderService.getOrder(username, pageable);
    }

    public OrderDto createOrder(@Valid CreateNewOrderRequest request) {
        return orderService.createOrder(request);
    }

    public OrderDto returnOrder(@Valid ProductReturnRequest request) {
        return orderService.returnOrder(request);
    }

    public OrderDto payOrder(UUID orderId) {
        return orderService.payOrder(orderId);
    }

    public OrderDto payOrderFailed(UUID orderId) {
        return orderService.payOrderFailed(orderId);
    }

    public OrderDto deliverOrder(UUID orderId) {
        return orderService.deliverOrder(orderId);
    }

    public OrderDto deliverOrderFailed(UUID orderId) {
        return orderService.deliverOrderFailed(orderId);
    }

    public OrderDto completeOrder(UUID orderId) {
        return orderService.completeOrder(orderId);
    }

    public OrderDto calculateTotalOrder(UUID orderId) {
        return orderService.calculateTotalOrder(orderId);
    }

    public OrderDto calculateDeliveryOrder(UUID orderId) {
        return orderService.calculateDeliveryOrder(orderId);
    }

    public OrderDto assemblyOrder(UUID orderId) {
        return orderService.assemblyOrder(orderId);
    }

    public OrderDto assemblyOrderFailed(UUID orderId) {
        return orderService.assemblyOrderFailed(orderId);
    }
}