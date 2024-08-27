package com.example.eCommerceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "tbl_shopping_cart")
public class ShoppingCartEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long shopId;
    private Long productId;
    private String nameProduct;
    private Integer quantity;
    private Integer price;
    private Integer totalPrice;
    private String image;
    private LocalDateTime createAt;
}
