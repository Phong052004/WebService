package com.example.eCommerceApp.dto.shoppingcart;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductInCartOutput {
    private Long id;
    private Long shoppingCartId;
    private Long productId;
    private String name;
    private Integer quantity;
    private Integer price;
    private String imageUrl;
    private Integer discountedPrice;
    private Integer totalPrice;
}
