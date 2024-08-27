package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.ShippingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingRepository extends JpaRepository<ShippingEntity, Long> {
}
