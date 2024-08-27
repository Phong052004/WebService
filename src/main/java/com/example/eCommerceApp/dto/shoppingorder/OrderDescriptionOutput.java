package com.example.eCommerceApp.dto.shoppingorder;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OrderDescriptionOutput {
    private Long id;
    private String fullName;
    private Long telephoneNumber;
    private String address;
    private List<ProductOrderInput> productOrderOutputs;
    private String state;
    private Integer shippingPrice;
    private Integer saleOffShop;
    private Integer totalPrice;
}
