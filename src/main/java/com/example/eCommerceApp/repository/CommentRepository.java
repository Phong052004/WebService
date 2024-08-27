package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<CommentEntity,Long> {
    CommentEntity findByIdAndUserId(Long commentId, Long userId);

    void deleteByIdAndUserId(Long commentId, Long userId);

    Page<CommentEntity> findAllByProductTemplateId(Long productTemplateId, Pageable pageable);
}
