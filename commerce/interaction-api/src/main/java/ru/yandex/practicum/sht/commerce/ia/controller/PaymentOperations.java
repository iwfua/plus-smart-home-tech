package ru.yandex.practicum.sht.commerce.ia.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.sht.commerce.ia.dto.order.OrderDto;
import ru.yandex.practicum.sht.commerce.ia.dto.payment.PaymentDto;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
public interface PaymentOperations {
    @PostMapping
    PaymentDto createPayment(@RequestParam @Valid OrderDto order);

    @PostMapping("/totalCost")
    BigDecimal calculateTotalCoast(@RequestParam @Valid OrderDto order);

    @PostMapping("/refund")
    void refundPayment(@RequestBody UUID paymentId);

    @PostMapping("/productCost")
    BigDecimal calculateProductCoast(@RequestParam @Valid OrderDto order);

    @PostMapping("/failed")
    void failPayment(@RequestBody UUID paymentId);
}