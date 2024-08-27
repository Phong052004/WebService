package com.example.eCommerceApp.dto.voucher;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
@Builder
public class VoucherOutput {
    private Long id;
    private Long voucherId;
    private String name;
    private String code;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private Float saleOffFloat;
    private Integer saleOffInteger;
    private Integer minValueOrder;
    private Integer discountedPrice;
}
