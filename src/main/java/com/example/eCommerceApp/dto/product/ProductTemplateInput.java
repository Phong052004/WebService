package com.example.eCommerceApp.dto.product;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductTemplateInput {
    private String name;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer quantity;
    private String description;
    private String sizeConvertImage;
}
