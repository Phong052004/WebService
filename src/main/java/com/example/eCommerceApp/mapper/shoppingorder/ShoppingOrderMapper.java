package com.example.eCommerceApp.mapper.shoppingorder;

import com.example.eCommerceApp.dto.shoppingorder.ShoppingOrderInput;
import com.example.eCommerceApp.dto.shoppingorder.ShoppingOrderOutput;
import com.example.eCommerceApp.entity.shoppingorder.ShoppingOrderEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ShoppingOrderMapper {
    ShoppingOrderEntity getEntityFromInput(ShoppingOrderInput shoppingOrderInput);

    ShoppingOrderOutput getOutputFromEntity(ShoppingOrderEntity shoppingOrderEntity);
}
