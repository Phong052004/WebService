package com.example.eCommerceApp.dto.shoppingorder;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ShoppingOrderInput {
    private String fullName;
    private String address;
    private Long phoneNumber;
    private Integer totalPrice;
    private String paymentMethod;
    private List<ProductsInput> productsInputs;
}
