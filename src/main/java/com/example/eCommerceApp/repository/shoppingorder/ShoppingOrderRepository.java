package com.example.eCommerceApp.repository.shoppingorder;

import com.example.eCommerceApp.entity.shoppingorder.ShoppingOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShoppingOrderRepository extends JpaRepository<ShoppingOrderEntity, Long> {
    Page<ShoppingOrderEntity> findAllByUserId(Long userId, Pageable pageable);
}
