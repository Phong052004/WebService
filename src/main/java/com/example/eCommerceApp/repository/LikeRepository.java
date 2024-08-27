package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.LikeEntity;
import org.mapstruct.Mapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LikeRepository extends JpaRepository<LikeEntity, Long> {
    boolean existsByProductTemplateIdAndUserId(Long productTemplateId, Long userId);
}
