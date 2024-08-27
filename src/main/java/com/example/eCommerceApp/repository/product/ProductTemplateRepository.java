package com.example.eCommerceApp.repository.product;

import com.example.eCommerceApp.entity.product.ProductTemplateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductTemplateRepository extends JpaRepository<ProductTemplateEntity, Long> {
    Page<ProductTemplateEntity> findAllByShopId(Long shopId, Pageable pageable);

    List<ProductTemplateEntity> findAllByIdIn(Set<Long> productTemplateIds);

    @Query("SELECT u FROM ProductTemplateEntity u WHERE u.name LIKE %?1%")
    Page<ProductTemplateEntity> searchProductTemplateEntitiesByString(String search, Pageable pageable);

    Page<ProductTemplateEntity> findAllBySubCategoryIdIn(List<Long> subCategoryId, Pageable pageable);

    Page<ProductTemplateEntity> findAllBySubCategoryId(Long subCategoryId, Pageable pageable);
}
