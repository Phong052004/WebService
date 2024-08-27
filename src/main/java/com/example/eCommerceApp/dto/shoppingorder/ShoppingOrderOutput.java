package com.example.eCommerceApp.dto.shoppingorder;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ShoppingOrderOutput {
    private Long shopId;
    private String nameShop;
    private List<ProductOrderInput> productOrderInputs;
    private String state;
    private Integer totalPrice;
}
