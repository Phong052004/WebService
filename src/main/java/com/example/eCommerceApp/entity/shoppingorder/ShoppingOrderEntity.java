package com.example.eCommerceApp.entity.shoppingorder;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
@Table(name = "tbl_shopping_order")
public class ShoppingOrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String fullName;
    private String address;
    private Long phoneNumber;
    private Long shopId;
    private String nameShop;
    private String state;
    private Integer totalPrice;
    private LocalDateTime createAt;
    private Integer shippingPrice;
    private Integer saleOffShop;
    private String paymentMethod;
}
