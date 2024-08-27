package com.example.eCommerceApp.repository.product;

import com.example.eCommerceApp.entity.product.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    List<ProductEntity> findAllByProductTemplateId(Long productTemplateId);

    void deleteAllByProductTemplateId(Long productTemplateId);

    void deleteAllByIdIn(List<Long> productIds);

    List<ProductEntity> findAllByIdIn(List<Long> productIds);

    List<ProductEntity> findAllByIdIn(Set<Long> productIds);
}
