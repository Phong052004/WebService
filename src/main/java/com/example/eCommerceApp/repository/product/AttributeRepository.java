package com.example.eCommerceApp.repository.product;

import com.example.eCommerceApp.entity.product.AttributeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface AttributeRepository extends JpaRepository<AttributeEntity,Long> {
    List<AttributeEntity> findAllByIsOfShop(Boolean False);

    List<AttributeEntity> findAllByIdIn(Set<Long> attributeIds);

    void deleteAllByIdInAndIsOfShop(Set<Long> attributeIds, Boolean True);
}
