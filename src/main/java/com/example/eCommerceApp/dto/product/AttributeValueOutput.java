package com.example.eCommerceApp.dto.product;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AttributeValueOutput {
    private Long id;
    private Long attributeValueId;
    private String name;
    private Boolean isOfShop;
    private Boolean existed;
}
