package ru.yandex.practicum.sht.commerce.sc.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.sht.commerce.ia.controller.ShoppingCartOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.sc.ChangeProductQuantityRequest;
import ru.yandex.practicum.sht.commerce.ia.dto.sc.ShoppingCartDto;
import ru.yandex.practicum.sht.commerce.sc.service.ShoppingCartService;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
public class ShoppingCartController implements ShoppingCartOperations {

    private final ShoppingCartService shoppingCartService;

    public ShoppingCartDto getShoppingCart(@NotNull String username) {
        return shoppingCartService.getShoppingCart(username);
    }

    public ShoppingCartDto addProductsToShoppingCart(@NotNull String username, @NotNull Map<UUID, Long> products) {
        return shoppingCartService.addProductsToShoppingCart(username, products);
    }

    public void deactivateShoppingCart(@NotNull String username) {
        shoppingCartService.deactivateShoppingCart(username);
    }

    public ShoppingCartDto removeProductsFromShoppingCart(@NotNull String username, @NotNull Set<UUID> products) {
        return shoppingCartService.removeProductsFromShoppingCart(username, products);
    }

    public ShoppingCartDto changeProductQuantity(@NotNull String username, @Valid ChangeProductQuantityRequest productQuantity) {
        return shoppingCartService.changeProductQuantity(username, productQuantity);
    }
}