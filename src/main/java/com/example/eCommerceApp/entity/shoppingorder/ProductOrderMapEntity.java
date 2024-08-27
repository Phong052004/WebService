package com.example.eCommerceApp.entity.shoppingorder;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "tbl_product_shopping_order")
public class ProductOrderMapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shoppingOrderId;
    private Long productId;
    private String name;
    private Integer quantityOrder;
    private Integer price;
    private String image;
    private Integer totalPrice;
    private Boolean isCheck;
}
