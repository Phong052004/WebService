package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.UserVoucherEntity;
import com.example.eCommerceApp.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucherEntity, Long> {

    List<UserVoucherEntity> findAllByUserId(Long userId);
}
