package com.example.eCommerceApp.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductInput {
    private Long productId;
    private String name;
    private Integer quantity;
    private Integer price;
    private String imageUrl;
    private List<Long> attributeValueIds;
    private Boolean existed;
}
