package com.example.eCommerceApp.dto.voucher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VoucherInput {
    private Long productId;
    private String name;
    private String code;
    private LocalDateTime startDate;
    private LocalDateTime expirationDate;
    private Float saleOffFloat;
    private Integer saleOffInteger;
    private Integer minValueOrder;
}
