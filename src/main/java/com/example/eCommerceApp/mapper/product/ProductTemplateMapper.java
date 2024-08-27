package com.example.eCommerceApp.mapper.product;

import com.example.eCommerceApp.dto.product.ProductTemplateInput;
import com.example.eCommerceApp.entity.product.ProductTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface ProductTemplateMapper {
    ProductTemplateEntity getEntityFromInput(ProductTemplateInput productTemplateInput);

    void updateEntityFromInput(@MappingTarget ProductTemplateEntity productTemplateEntity,
                               ProductTemplateInput productTemplateInput);
}
