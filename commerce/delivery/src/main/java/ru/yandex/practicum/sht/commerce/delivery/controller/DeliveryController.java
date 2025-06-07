package ru.yandex.practicum.sht.commerce.delivery.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.sht.commerce.delivery.service.DeliveryService;
import ru.yandex.practicum.sht.commerce.ia.controller.DeliveryOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.delivery.DeliveryDto;
import ru.yandex.practicum.sht.commerce.ia.dto.order.OrderDto;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class DeliveryController implements DeliveryOperations {

    private final DeliveryService deliveryService;

    public DeliveryDto createDelivery(@Valid DeliveryDto request) {
        return deliveryService.createDelivery(request);
    }

    public void successDelivery(UUID orderId) {
        deliveryService.successDelivery(orderId);
    }

    public void pickDelivery(UUID orderId) {
        deliveryService.pickDelivery(orderId);
    }

    public void failDelivery(UUID orderId) {
        deliveryService.failDelivery(orderId);
    }

    public BigDecimal costDelivery(@Valid OrderDto request) {
        return deliveryService.costDelivery(request);
    }
}