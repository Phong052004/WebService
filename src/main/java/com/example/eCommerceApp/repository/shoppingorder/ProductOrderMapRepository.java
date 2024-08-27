package com.example.eCommerceApp.repository.shoppingorder;

import com.example.eCommerceApp.entity.shoppingorder.ProductOrderMapEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductOrderMapRepository extends JpaRepository<ProductOrderMapEntity, Long> {
    List<ProductOrderMapEntity> findAllByShoppingOrderIdIn(List<Long> shoppingOrderIds);

    List<ProductOrderMapEntity> findAllByShoppingOrderId(Long shoppingOrderId);

    List<ProductOrderMapEntity> findAllByIsCheck(Boolean False);
}
