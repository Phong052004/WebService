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
public class AttributeInput {
    private Long attributeId;
    private String name;
    private List<AttributeValueInput> attributeValues;
    private Boolean existed;
}
