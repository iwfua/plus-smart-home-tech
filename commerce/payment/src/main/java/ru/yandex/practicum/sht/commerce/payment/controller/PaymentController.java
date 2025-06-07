package ru.yandex.practicum.sht.commerce.payment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.sht.commerce.ia.controller.PaymentOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.order.OrderDto;
import ru.yandex.practicum.sht.commerce.ia.dto.payment.PaymentDto;
import ru.yandex.practicum.sht.commerce.payment.service.PaymentService;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
public class PaymentController implements PaymentOperations {

    private final PaymentService paymentService;

    public PaymentDto createPayment(@Valid OrderDto order) {
        return paymentService.createPayment(order);
    }

    public BigDecimal calculateTotalCoast(@Valid OrderDto order) {
        return paymentService.calculateTotalCoast(order);
    }

    public void refundPayment(UUID paymentId) {
        paymentService.refundPayment(paymentId);
    }

    public BigDecimal calculateProductCoast(@Valid OrderDto order) {
        return paymentService.calculateProductCoast(order);
    }

    public void failPayment(UUID paymentId) {
        paymentService.failPayment(paymentId);
    }
}