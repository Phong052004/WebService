package com.example.eCommerceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
@Table(name = "tbl_voucher")
public class VoucherEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String code;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private Float saleOffFloat;
    private Integer saleOffInteger;
    private Integer minValueOrder;
    private Integer discountedPrice;
    private Long shopId;
    private Long productTemplateId;
    private Long productId;
    private Boolean isVoucherShop;
    private Boolean isGlobal;
}
