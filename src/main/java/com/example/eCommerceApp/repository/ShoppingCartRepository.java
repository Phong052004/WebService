package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.ShoppingCartEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCartEntity, Long> {
    ShoppingCartEntity findByProductId(Long productId);

    Page<ShoppingCartEntity> findAllByUserId(Long userId, Pageable pageable);

    void deleteAllByProductIdIn(List<Long> productIds);

    List<ShoppingCartEntity> findAllByProductIdIn(List<Long> productIds);

    List<ShoppingCartEntity> findAllByIdIn(List<Long> shoppingCartIds);

    void deleteAllByIdIn(List<Long> shoppingCartIds);
}
