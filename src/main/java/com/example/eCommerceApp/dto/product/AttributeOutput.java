package com.example.eCommerceApp.dto.product;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AttributeOutput {
    private Long id;
    private Long attributeId;
    private String name;
    private List<AttributeValueOutput> attributeValueOutputs;
    private Boolean isOfShop;
    private Boolean existed;
}
