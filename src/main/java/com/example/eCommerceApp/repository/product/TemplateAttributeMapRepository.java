package com.example.eCommerceApp.repository.product;

import com.example.eCommerceApp.entity.product.TemplateAttributeMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateAttributeMapRepository extends JpaRepository<TemplateAttributeMap, Long> {
    void deleteAllByProductTemplateId(Long productTemplateId);

    List<TemplateAttributeMap> findAllByProductTemplateId(Long productTemplateId);
}
