package ru.yandex.practicum.sht.commerce.warehouse.service;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.sht.commerce.ia.dto.sc.ShoppingCartDto;
import ru.yandex.practicum.sht.commerce.ia.dto.warehouse.*;

import java.util.Map;
import java.util.UUID;

public interface WarehouseService {

    void addProductToWarehouse(@RequestBody @Valid NewProductInWarehouseRequest warehouseRequest);

    BookedProductsDto checkProductQuantity(ShoppingCartDto shoppingCartDto);

    void acceptProductToWarehouse(AddProductToWarehouseRequest warehouseRequest);

    void shippedToDelivery(ShippedToDeliveryRequest request);

    void acceptReturn(Map<UUID, Long> products);

    BookedProductsDto assemblyProducts(AssemblyProductsForOrderRequest request);

    AddressDto getWarehouseAddress();
}
