package com.example.eCommerceApp.repository.product;

import com.example.eCommerceApp.entity.product.ProductAttributeValueMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAttributeValueMapRepository extends JpaRepository<ProductAttributeValueMapEntity,Long> {
    List<ProductAttributeValueMapEntity> findAllByProductTemplateId(Long productTemplateId);

    void deleteAllByProductTemplateId(Long productTemplateId);

    List<ProductAttributeValueMapEntity> findAllByProductIdIn(List<Long> productIds);

    List<ProductAttributeValueMapEntity> findAllByAttributeValueIdInAndProductTemplateId(List<Long> attributeValueIds,
                                                                                         Long productTemplateId);

    List<ProductAttributeValueMapEntity> findAllByAttributeId(Long attributeId);

    void deleteAllByProductIdIn(List<Long> productIds);

}
