package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.shoppingcart.*;
import com.example.eCommerceApp.service.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/shopping-cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Thêm sản phẩm vào giỏ hàng")
    @PostMapping("/add")
    public void addProductIntoCart(@RequestHeader("Authorization") String accessToken,
                                   @RequestBody ShoppingCartInput shoppingCartInput) {
        shoppingCartService.addProductIntoCart(accessToken, shoppingCartInput);
    }

    @Operation(summary = "Thay đổi thông tin sản phẩm trong giỏ hàng")
    @PostMapping("/update")
    public void changeInfoProductInCart(@RequestHeader("Authorization") String accessToken,
                                        @RequestParam Long shoppingCartId,
                                        @RequestBody ChangeProductInCartInput changeProductInCartInput) {
        shoppingCartService.changeProductInCart(accessToken, shoppingCartId, changeProductInCartInput);
    }

    @Operation(summary = "Xóa sản phẩm trong giỏ hàng")
    @DeleteMapping("/delete")
    public void deleteProductInCart(@RequestHeader("Authorization") String accessToken,
                                    @RequestParam Long shoppingCartId) {
        shoppingCartService.deleteProductInCart(accessToken,shoppingCartId);
    }

    @Operation(summary = "Lấy ra sản phẩm trong giỏ hàng")
    @GetMapping("/get-products")
    public Page<ShoppingCartOutput> getProductsInCart(@RequestHeader("Authorization") String accessToken,
                                                      @ParameterObject Pageable pageable) {
        return shoppingCartService.getProductsInCart(accessToken, pageable);
    }

    @Operation(summary = "Lấy ra sản phẩm chuẩn bị order")
    @GetMapping("/get-products-order")
    public ShoppingCartOrderOutput getProductsBeforeOrdering(@RequestHeader("Authorization") String accessToken,
                                                    @RequestBody ShoppingCartOrderInput shoppingCartOrderInput) {
        return shoppingCartService.getProductBeforeOrdering(accessToken, shoppingCartOrderInput);
    }
}
