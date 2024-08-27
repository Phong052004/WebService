package com.example.eCommerceApp.mapper.shoppingorder;

import com.example.eCommerceApp.dto.shoppingorder.ProductOrderInput;
import com.example.eCommerceApp.entity.shoppingorder.ProductOrderMapEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ProductOrderMapMapper {
    ProductOrderMapEntity getEntityFromInput(ProductOrderInput productOrderInput);

    ProductOrderInput getOutputFromEntity(ProductOrderMapEntity productOrderMapEntity);
}
