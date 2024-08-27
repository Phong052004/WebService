package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.shoppingorder.OrderDescriptionOutput;
import com.example.eCommerceApp.dto.shoppingorder.ShoppingOrderInput;
import com.example.eCommerceApp.dto.shoppingorder.ShoppingOrderOutput;
import com.example.eCommerceApp.service.ShoppingOrderService;
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
@RequestMapping("/api/v1/shopping-order")
public class ShoppingOrderController {
    private final ShoppingOrderService shoppingOrderService;

    @Operation(summary = "Đặt hàng")
    @PostMapping("/order")
    public void orderProducts(@RequestHeader("Authorization") String accessToken,
                              @RequestBody ShoppingOrderInput shoppingOrderInput) {
        shoppingOrderService.orderProducts(accessToken,shoppingOrderInput);
    }

    @Operation(summary = "Xem đơn hàng")
    @GetMapping("/get-orders")
    public Page<ShoppingOrderOutput> getAllOrder(@RequestHeader("Authorization") String accessToken,
                                                        @ParameterObject Pageable pageable) {
        return shoppingOrderService.getAllProductOrder(accessToken, pageable);
    }

    @Operation(summary = "Mua lại sản phẩm")
    @PostMapping("/repurchase")
    public void repurchase(@RequestHeader("Authorization") String accessToken,
                           @RequestParam Long shoppingOrderId) {
        shoppingOrderService.repurchase(accessToken, shoppingOrderId);
    }

    @Operation(summary = "update số lượng sản phẩm")
    @PostMapping("/calculate-quantity")
    public void updateProductsAfterOrder() {
        shoppingOrderService.updateQuantityProductAfterOrder();
    }

    @Operation(summary = "Lấy đơn hàng theo trạng thái")
    @GetMapping("/get-orders-by-state")
    public Page<ShoppingOrderOutput> getOrdersByState(@RequestHeader("Authorization") String accessToken,
                                                      @RequestParam String state,
                                                      @ParameterObject Pageable pageable) {
        return shoppingOrderService.getProductsByState(accessToken, pageable, state);
    }

    @Operation(summary = "Xem chi tiết đơn hàng")
    @GetMapping("/order-details")
    public OrderDescriptionOutput getOrderDetails(@RequestHeader("Authorization") String accessToken,
                                                  @RequestParam Long shoppingOrderId) {
        return shoppingOrderService.getDetailsOrder(accessToken,shoppingOrderId);
    }

    @Operation(summary = "Hủy đơn hàng")
    @PostMapping("/cancel")
    public void cancelOrder(@RequestHeader("Authorization") String accessToken,
                            @RequestParam Long orderId) {
        shoppingOrderService.cancelOrder(accessToken, orderId);
    }
}
