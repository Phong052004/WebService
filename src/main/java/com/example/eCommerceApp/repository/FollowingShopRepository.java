package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.FollowingShopEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FollowingShopRepository extends JpaRepository<FollowingShopEntity, Long> {
}
