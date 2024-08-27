package com.example.eCommerceApp.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ProductOutPut {
    private Long id;
    private Long productId;
    private String name;
    private Integer quantity;
    private Integer price;
    private String imageUrl;
    private Integer discountedPrice;
    private Integer saleOffInteger;
    private Float saleOffFloat;
    private Boolean existed;
}
