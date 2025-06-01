package ru.yandex.practicum.sht.commerce.warehouse.controller;

import jakarta.validation.Valid;
import ru.yandex.practicum.sht.commerce.ia.controller.WarehouseOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.sc.ShoppingCartDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.AddProductToWarehouseRequest;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.AddressDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.NewProductInWarehouseRequest;
import ru.yandex.practicum.sht.commerce.warehouse.service.WarehouseService;

public class WarehouseController implements WarehouseOperations {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    public void addProductToWarehouse(@Valid NewProductInWarehouseRequest warehouseRequest) {
        warehouseService.addProductToWarehouse(warehouseRequest);
    }

    public BookedProductsDto checkProductQuantity(@Valid ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkProductQuantity(shoppingCartDto);
    }

    public void acceptProductToWarehouse(@Valid AddProductToWarehouseRequest warehouseRequest) {
        warehouseService.acceptProductToWarehouse(warehouseRequest);
    }

    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }
}