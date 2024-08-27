package com.example.eCommerceApp.mapper.product;

import com.example.eCommerceApp.dto.product.ProductInput;
import com.example.eCommerceApp.entity.product.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductMapper {
    ProductEntity getEntityFromInput(ProductInput productInput);

    void updateEntityFormInput(@MappingTarget ProductEntity productEntity, ProductInput productInput);
}
