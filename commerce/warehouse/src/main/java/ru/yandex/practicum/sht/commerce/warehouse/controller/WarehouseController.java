package ru.yandex.practicum.sht.commerce.warehouse.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.sht.commerce.ia.controller.WarehouseOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.sc.ShoppingCartDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.*;
import ru.yandex.practicum.sht.commerce.warehouse.service.WarehouseService;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    public void addProductToWarehouse(@Valid NewProductInWarehouseRequest warehouseRequest) {
        warehouseService.addProductToWarehouse(warehouseRequest);
    }

    public BookedProductsDto checkProductQuantity(@Valid ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantity(shoppingCartDto);
    }

    public void acceptProductToWarehouse(@Valid AddProductToWarehouseRequest warehouseRequest) {
        warehouseService.acceptProductToWarehouse(warehouseRequest);
    }

    public void shippedToDelivery(@RequestBody @Valid ShippedToDeliveryRequest request) {
        warehouseService.shippedToDelivery(request);
    }

    public void acceptReturn(@RequestBody Map<UUID, Long> products) {
        warehouseService.acceptReturn(products);
    }

    public BookedProductsDto assemblyProducts(@RequestBody @Valid AssemblyProductsForOrderRequest request) {
        return warehouseService.assemblyProducts(request);
    }

    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}