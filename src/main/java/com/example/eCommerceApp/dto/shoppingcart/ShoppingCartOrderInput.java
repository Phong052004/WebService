package com.example.eCommerceApp.dto.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class ShoppingCartOrderInput {
    private String fullName;
    private String address;
    private Long phoneNumber;
    private List<Long> shoppingCartIds;
}
