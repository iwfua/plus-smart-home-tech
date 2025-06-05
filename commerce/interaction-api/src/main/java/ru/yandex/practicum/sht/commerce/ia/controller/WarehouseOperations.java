package ru.yandex.practicum.sht.commerce.ia.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.sht.commerce.ia.dto.sc.ShoppingCartDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.AddressDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.NewProductInWarehouseRequest;

@RestController
@RequestMapping("/api/v1/warehouse")
public interface WarehouseOperations {
    @PutMapping
    void addProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequest warehouseRequest);

    @PostMapping("/check")
    BookedProductsDto checkProductQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void acceptProductToWarehouse(@RequestBody @Valid AddProductToWarehouseRequest warehouseRequest);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}