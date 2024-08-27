package com.example.eCommerceApp.mapper;

import com.example.eCommerceApp.dto.voucher.VoucherInput;
import com.example.eCommerceApp.dto.voucher.VoucherOutput;
import com.example.eCommerceApp.entity.VoucherEntity;
import org.mapstruct.Mapper;

@Mapper
public interface VoucherMapper {
    VoucherEntity getEntityFromInput(VoucherInput voucherInput);

    VoucherOutput getOutputFromEntity(VoucherEntity voucherEntity);
}
