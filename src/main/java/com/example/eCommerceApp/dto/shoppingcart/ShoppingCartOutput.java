package com.example.eCommerceApp.dto.shoppingcart;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ShoppingCartOutput {
    private Long id;
    private Long shopId;
    private String nameShop;
    private List<ProductInCartOutput> productInCartOutputs;
    private Integer totalPrice;
}
