package com.example.eCommerceApp.dto.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ShoppingCartInput {
    private Long productId;
    private Long shopId;
    private Integer quantity;
}
