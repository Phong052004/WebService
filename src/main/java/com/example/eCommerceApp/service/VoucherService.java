package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.voucher.VoucherInput;
import com.example.eCommerceApp.dto.voucher.VoucherOutput;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.entity.UserVoucherEntity;
import com.example.eCommerceApp.entity.VoucherEntity;
import com.example.eCommerceApp.entity.product.ProductEntity;
import com.example.eCommerceApp.entity.product.ProductTemplateEntity;
import com.example.eCommerceApp.mapper.VoucherMapper;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.UserVoucherRepository;
import com.example.eCommerceApp.repository.VoucherRepository;
import com.example.eCommerceApp.repository.product.ProductRepository;
import com.example.eCommerceApp.repository.product.ProductTemplateRepository;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final CustomRepository customRepository;
    private final VoucherMapper voucherMapper;
    private final UserVoucherRepository userVoucherRepository;
    private final ProductRepository productRepository;

    @Transactional
    public void createVoucherShop(String accessToken, VoucherInput voucherInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        VoucherEntity voucherShopEntity = voucherMapper.getEntityFromInput(voucherInput);
        voucherShopEntity.setShopId(shopId);
        voucherShopEntity.setIsGlobal(Boolean.FALSE);
        voucherShopEntity.setIsVoucherShop(Boolean.TRUE);
        voucherRepository.save(voucherShopEntity);
    }

    @Transactional
    public void createVoucherProductTemplate(String accessToken, VoucherInput voucherInput, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(!productTemplateEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        VoucherEntity voucherProductTemplateEntity = voucherMapper.getEntityFromInput(voucherInput);
        voucherProductTemplateEntity
                .setDiscountedPrice(productTemplateEntity.getMaxPrice() - voucherInput.getSaleOffInteger());
        voucherProductTemplateEntity.setProductTemplateId(productTemplateId);
        voucherProductTemplateEntity.setShopId(shopId);
        voucherProductTemplateEntity.setIsGlobal(Boolean.FALSE);
        voucherProductTemplateEntity.setIsVoucherShop(Boolean.FALSE);
        voucherRepository.save(voucherProductTemplateEntity);
    }

    @Transactional
    public void createVoucherProduct(String accessToken, List<VoucherInput> voucherInputs) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<Long> productIds = voucherInputs.stream().map(VoucherInput::getProductId).collect(Collectors.toList());

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        for(VoucherInput voucherInput : voucherInputs) {
            ProductEntity productEntity = productEntityMap.get(voucherInput.getProductId());
            VoucherEntity voucherProductEntity = voucherMapper.getEntityFromInput(voucherInput);
            voucherProductEntity.setDiscountedPrice(productEntity.getPrice() - voucherInput.getSaleOffInteger());
            voucherProductEntity.setIsGlobal(Boolean.FALSE);
            voucherProductEntity.setShopId(shopId);
            voucherProductEntity.setIsVoucherShop(Boolean.FALSE);
            voucherRepository.save(voucherProductEntity);
        }
    }

    @Transactional
    public void deleteVoucher(String accessToken, Long voucherId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        VoucherEntity voucherEntity = customRepository.getVoucherBy(voucherId);
        if(!voucherEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        voucherRepository.deleteById(voucherId);
    }

    @Transactional(readOnly = true)
    public List<VoucherOutput> getVouchersShop(Long shopId) {
        List<VoucherEntity> voucherShopEntities = voucherRepository.findAllByShopIdAndIsVoucherShop(shopId, Boolean.TRUE);
        if(voucherShopEntities.isEmpty()) {
            return null;
        }

        List<VoucherOutput> voucherOutputs = new ArrayList<>();
        for(VoucherEntity voucherEntity : voucherShopEntities) {
            VoucherOutput voucherOutput = voucherMapper.getOutputFromEntity(voucherEntity);
            voucherOutputs.add(voucherOutput);
        }

        return voucherOutputs;
    }

    @Transactional(readOnly = true)
    public VoucherOutput getVoucherProductTemplate(Long productTemplateId) {
        VoucherEntity voucherEntity = voucherRepository.findByProductTemplateId(productTemplateId);

        return voucherMapper.getOutputFromEntity(voucherEntity);
    }

    @Transactional(readOnly = true)
    public List<VoucherOutput> getVoucherProducts(Long productTemplateId) {
        List<Long> productIds = productRepository.findAllByProductTemplateId(productTemplateId)
                .stream().map(ProductEntity::getId).collect(Collectors.toList());

        List<VoucherEntity> voucherProductEntities = voucherRepository.findAllByProductIdIn(productIds);
        if(voucherProductEntities.isEmpty()) {
            return null;
        }

        List<VoucherOutput> voucherOutputs = new ArrayList<>();
        for(VoucherEntity voucherEntity : voucherProductEntities) {
            VoucherOutput voucherOutput = voucherMapper.getOutputFromEntity(voucherEntity);
            voucherOutputs.add(voucherOutput);
        }

        return voucherOutputs;
    }

    @Transactional
    public void addVoucherShop(String accessToken, Long shopId, Long voucherId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        VoucherEntity voucherEntity = customRepository.getVoucherBy(voucherId);

        if(!voucherEntity.getShopId().equals(shopId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        UserVoucherEntity userVoucherEntity = UserVoucherEntity.builder()
                .userId(userId)
                .voucherId(voucherId)
                .build();

        userVoucherRepository.save(userVoucherEntity);
    }

    @Transactional(readOnly = true)
    public List<VoucherOutput> getVouchersBy(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        List<Long> voucherIds = userVoucherRepository.findAllByUserId(userId).stream()
                .map(UserVoucherEntity::getVoucherId).collect(Collectors.toList());

        List<VoucherEntity> voucherEntities = voucherRepository.findAllByIdIn(voucherIds);

        List<VoucherOutput> voucherOutputs = new ArrayList<>();
        for(VoucherEntity voucherEntity : voucherEntities) {
            VoucherOutput voucherOutput = voucherMapper.getOutputFromEntity(voucherEntity);
            voucherOutputs.add(voucherOutput);
        }

        return voucherOutputs;
    }
}
