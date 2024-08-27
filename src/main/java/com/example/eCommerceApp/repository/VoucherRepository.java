package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.VoucherEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface VoucherRepository extends JpaRepository<VoucherEntity, Long> {
    List<VoucherEntity> findAllByShopIdAndIsVoucherShop(Long shopId, Boolean True);

    VoucherEntity findByProductTemplateId(Long productTemplateId);

    List<VoucherEntity> findAllByProductIdIn(List<Long> productIds);

    void deleteAllByProductIdIn(List<Long> productIds);

    void deleteByProductTemplateId(Long productTemplateId);

    List<VoucherEntity> findAllByProductTemplateIdIn(List<Long> productTemplateIds);

    VoucherEntity findByProductId(Long productId);

    List<VoucherEntity> findAllByShopIdInAndIsVoucherShop(Set<Long> shopIds, Boolean True);

    List<VoucherEntity> findAllByIdIn(List<Long> voucherIds);
}
