package com.example.eCommerceApp.entity.product;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_attribute_value")
@Builder
public class AttributeValueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long attributeId;
    private String name;
    private Boolean isOfShop;
}
