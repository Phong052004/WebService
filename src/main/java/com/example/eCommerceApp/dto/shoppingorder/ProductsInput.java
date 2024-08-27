package com.example.eCommerceApp.dto.shoppingorder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductsInput {
    private Long shopId;
    private String nameShop;
    private Integer shippingPrice;
    private Integer saleOffShop;
    private List<ProductOrderInput> productOrderInputs;
}
