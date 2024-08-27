package com.example.eCommerceApp.dto.shoppingcart;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ShoppingCartOrderOutput {
    private Long userId;
    private String address;
    private Long phoneNumber;
    private List<ShoppingCartOutput> shoppingCartOutputs;
    private Integer totalPrice;
}
