package ru.yandex.practicum.sht.commerce.ss.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import ru.yandex.practicum.sht.commerce.ia.controller.ShoppingStoreOperations;
import ru.yandex.practicum.sht.commerce.ia.dto.ss.ProductDto;
import ru.yandex.practicum.sht.commerce.ia.dto.ss.SetProductQuantityStateRequest;
import ru.yandex.practicum.sht.commerce.ia.dto.ss.dict.ProductCategory;
import ru.yandex.practicum.sht.commerce.ss.service.ProductService;

import java.util.List;
import java.util.UUID;

public class ProductController implements ShoppingStoreOperations {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    public List<ProductDto> getSortedProducts(@NotNull ProductCategory category, Pageable pageable) {
        return productService.getSortedProducts(category, pageable);
    }

    public ProductDto createProduct(@Valid ProductDto productDto) {
        return productService.createProduct(productDto);
    }

    public ProductDto updateProduct(@Valid ProductDto productDto) {
        return productService.updateProduct(productDto);
    }

    public boolean deleteProduct(@NotNull UUID productId) {
        return productService.deleteProduct(productId);
    }

    public boolean updateProductQuantityState(@Valid SetProductQuantityStateRequest quantityState) {
        return productService.updateProductQuantityState(quantityState);
    }

    public ProductDto getProduct(@NotNull UUID productId) {
        return productService.getProduct(productId);
    }
}