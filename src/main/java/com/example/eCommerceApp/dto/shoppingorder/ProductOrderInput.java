package com.example.eCommerceApp.dto.shoppingorder;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductOrderInput {
    private Long shoppingCartId;
    private Long productId;
    private String name;
    private Integer quantityOrder;
    private Integer price;
    private String image;
    private Integer totalPrice;
}
