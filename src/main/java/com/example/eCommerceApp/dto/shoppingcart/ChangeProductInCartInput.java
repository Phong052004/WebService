package com.example.eCommerceApp.dto.shoppingcart;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChangeProductInCartInput {
    private Long productTemplateId;
    private List<Long> attributeValueIds;
    private Integer quantity;
}
