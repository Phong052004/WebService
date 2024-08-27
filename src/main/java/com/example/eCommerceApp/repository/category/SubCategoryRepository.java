package com.example.eCommerceApp.repository.category;

import com.example.eCommerceApp.entity.category.SubCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategoryEntity, Long> {
    List<SubCategoryEntity> findAllByCategoryId(Long categoryId);
}
