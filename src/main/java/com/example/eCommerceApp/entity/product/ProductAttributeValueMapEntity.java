package com.example.eCommerceApp.entity.product;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_product_attribute_value_map")
@Builder
public class ProductAttributeValueMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productId;
    private Long attributeId;
    private Long attributeValueId;
    private Long productTemplateId;
}
