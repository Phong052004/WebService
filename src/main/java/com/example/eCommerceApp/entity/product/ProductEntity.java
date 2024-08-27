package com.example.eCommerceApp.entity.product;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_product")
@Builder
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long productTemplateId;
    private String name;
    private Integer quantity;
    private Integer price;
    private String imageUrl;
}
