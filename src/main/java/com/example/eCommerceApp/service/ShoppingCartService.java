package com.example.eCommerceApp.service;

import com.example.eCommerceApp.base.filter.Filter;
import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.product.ProductOutPut;
import com.example.eCommerceApp.dto.shoppingcart.*;
import com.example.eCommerceApp.entity.ShoppingCartEntity;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.entity.VoucherEntity;
import com.example.eCommerceApp.entity.product.ProductAttributeValueMapEntity;
import com.example.eCommerceApp.entity.product.ProductEntity;
import com.example.eCommerceApp.entity.shoppingorder.ShoppingOrderEntity;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.ShoppingCartRepository;
import com.example.eCommerceApp.repository.UserRepository;
import com.example.eCommerceApp.repository.VoucherRepository;
import com.example.eCommerceApp.repository.product.ProductAttributeValueMapRepository;
import com.example.eCommerceApp.repository.product.ProductRepository;
import com.example.eCommerceApp.token.TokenHelper;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ShoppingCartService {
    private final ShoppingCartRepository shoppingCartRepository;
    private final CustomRepository customRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final ProductAttributeValueMapRepository productAttributeValueMapRepository;
    private final UserRepository userRepository;
    private final EntityManager entityManager;

    @Transactional
    public void addProductIntoCart(String accessToken, ShoppingCartInput shoppingCartInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ProductEntity productEntity = customRepository.getProductBy(shoppingCartInput.getProductId());
        VoucherEntity voucherEntity = voucherRepository.findByProductId(shoppingCartInput.getProductId());
        ShoppingCartEntity shoppingCartEntityExisted = shoppingCartRepository
                .findByProductId(shoppingCartInput.getProductId());

        LocalDateTime now = LocalDateTime.now();

        if(Objects.isNull(shoppingCartEntityExisted)) {
            ShoppingCartEntity shoppingCartEntity = ShoppingCartEntity.builder()
                    .shopId(shoppingCartInput.getShopId())
                    .nameProduct(productEntity.getName())
                    .productId(shoppingCartInput.getProductId())
                    .quantity(shoppingCartInput.getQuantity())
                    .image(productEntity.getImageUrl())
                    .userId(userId)
                    .createAt(now)
                    .build();
            if(Objects.isNull(voucherEntity)) {
                shoppingCartEntity.setPrice(productEntity.getPrice());
                shoppingCartEntity.setTotalPrice(productEntity.getPrice() * shoppingCartInput.getQuantity());
            } else {
                shoppingCartEntity.setPrice(voucherEntity.getDiscountedPrice());
                shoppingCartEntity.setTotalPrice(voucherEntity.getDiscountedPrice() * shoppingCartInput.getQuantity());
            }
            shoppingCartRepository.save(shoppingCartEntity);
        } else {
            int quantity = shoppingCartEntityExisted.getQuantity() + shoppingCartInput.getQuantity();
            shoppingCartEntityExisted.setQuantity(quantity);
            shoppingCartEntityExisted.setCreateAt(now);
            if(Objects.isNull(voucherEntity)) {
                shoppingCartEntityExisted.setPrice(productEntity.getPrice());
                shoppingCartEntityExisted.setTotalPrice(productEntity.getPrice() * quantity);
            } else {
                shoppingCartEntityExisted.setPrice(voucherEntity.getDiscountedPrice());
                shoppingCartEntityExisted.setTotalPrice(voucherEntity.getDiscountedPrice() * quantity);
            }
            shoppingCartRepository.save(shoppingCartEntityExisted);
        }
    }

    @Transactional
    public void changeProductInCart(String accessToken,
                                    Long shoppingCartId,
                                    ChangeProductInCartInput changeProductInCartInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        ShoppingCartEntity shoppingCartEntity = customRepository.getShoppingCartBy(shoppingCartId);

        if(!shoppingCartEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByAttributeValueIdInAndProductTemplateId(
                        changeProductInCartInput.getAttributeValueIds(),
                        changeProductInCartInput.getProductTemplateId()
                );

        if(productAttributeValueMapEntities.isEmpty()) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<Long> productIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getProductId).collect(Collectors.toList());

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntityList =
                productAttributeValueMapRepository.findAllByProductIdIn(productIds);

        Map<Long, List<Long>> productEntityMap = productAttributeValueMapEntityList.stream()
                .collect(Collectors.groupingBy(
                                ProductAttributeValueMapEntity::getProductId,
                                Collectors.mapping(ProductAttributeValueMapEntity::getAttributeValueId, Collectors.toList())
                        )
                );

        long productId = 0;
        for(Long id : productIds) {
            List<Long> attributeValueIds = productEntityMap.get(id);
            if (!attributeValueIds.retainAll(changeProductInCartInput.getAttributeValueIds())) {
                productId = id;
                break;
            }
        }

        if(productId == 0) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductEntity productEntity = customRepository.getProductBy(productId);
        VoucherEntity voucherEntity = voucherRepository.findByProductId(productId);

        shoppingCartEntity.setProductId(productId);
        shoppingCartEntity.setQuantity(changeProductInCartInput.getQuantity());
        shoppingCartEntity.setCreateAt(LocalDateTime.now());
        shoppingCartEntity.setImage(productEntity.getImageUrl());
        shoppingCartEntity.setNameProduct(productEntity.getName());

        if(Objects.nonNull(voucherEntity)) {
            shoppingCartEntity.setPrice(voucherEntity.getDiscountedPrice());
            shoppingCartEntity.setTotalPrice(voucherEntity.getDiscountedPrice() * changeProductInCartInput.getQuantity());
        } else {
            shoppingCartEntity.setPrice(productEntity.getPrice());
            shoppingCartEntity.setTotalPrice(productEntity.getPrice() * changeProductInCartInput.getQuantity());
        }

        shoppingCartRepository.save(shoppingCartEntity);
    }

    @Transactional
    public void deleteProductInCart(String accessToken, Long shoppingCartId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        ShoppingCartEntity shoppingCartEntity = customRepository.getShoppingCartBy(shoppingCartId);

        if(!shoppingCartEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        shoppingCartRepository.deleteById(shoppingCartId);
    }

    @Transactional
    public Page<ShoppingCartOutput> getProductsInCart(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        Page<ShoppingCartEntity> shoppingCartEntities = Filter.builder(ShoppingCartEntity.class, entityManager)
                .search()
                .isEqual("userId", userId)
                .orderBy("createAt", Common.DESC)
                .getPage(pageable);

        List<Long> productIds = shoppingCartEntities.stream()
                .map(ShoppingCartEntity::getProductId)
                .collect(Collectors.toList());
        Set<Long> shopIds = shoppingCartEntities.stream()
                .map(ShoppingCartEntity::getShopId)
                .collect(Collectors.toSet());

        Map<Long, UserEntity> shopEntityMap = userRepository.findAllByIdIn(shopIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));
        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        Map<Long, VoucherEntity> voucherEntityMap = voucherRepository.findAllByProductIdIn(productIds)
                .stream().collect(Collectors.toMap(VoucherEntity::getProductId, Function.identity()));

        Map<Long, List<ShoppingCartEntity>> shoppingCartMap = shoppingCartEntities.stream()
                .collect(Collectors.groupingBy(ShoppingCartEntity::getShopId));

        List<ShoppingCartOutput> shoppingCartOutputs = new ArrayList<>();

        for(Long shopId : shopIds) {
            List<ShoppingCartEntity> shoppingCartEntityList = shoppingCartMap.get(shopId);
            List<ProductInCartOutput> productInCartOutputs = new ArrayList<>();
            UserEntity shopEntity = shopEntityMap.get(shopId);
            shoppingCartEntityList.forEach(
                    shoppingCartEntity -> {
                        ProductEntity productEntity = productEntityMap.get(shoppingCartEntity.getProductId());
                        VoucherEntity voucherEntity = voucherEntityMap.get(shoppingCartEntity.getProductId());

                        ProductInCartOutput productInCartOutput = ProductInCartOutput.builder()
                                .shoppingCartId(shoppingCartEntity.getId())
                                .productId(productEntity.getId())
                                .name(productEntity.getName())
                                .quantity(shoppingCartEntity.getQuantity())
                                .imageUrl(productEntity.getImageUrl())
                                .price(productEntity.getPrice())
                                .totalPrice(shoppingCartEntity.getTotalPrice())
                                .build();

                        if(Objects.nonNull(voucherEntity)) {
                            productInCartOutput.setDiscountedPrice(voucherEntity.getDiscountedPrice());
                        }

                        productInCartOutputs.add(productInCartOutput);
                    }
            );

            ShoppingCartOutput shoppingCartOutput = ShoppingCartOutput.builder()
                    .shopId(shopId)
                    .nameShop(shopEntity.getFullName())
                    .productInCartOutputs(productInCartOutputs)
                    .build();

            shoppingCartOutputs.add(shoppingCartOutput);
        }
        return new PageImpl<>(shoppingCartOutputs, pageable, shoppingCartEntities.getTotalElements());
    }

    @Transactional(readOnly = true)
    public ShoppingCartOrderOutput getProductBeforeOrdering(String accessToken, ShoppingCartOrderInput shoppingCartOrderInput) {
        Long userId =  TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);

        List<ShoppingCartEntity> shoppingCartEntities = shoppingCartRepository
                .findAllByIdIn(shoppingCartOrderInput.getShoppingCartIds());
        List<Long> productIds = shoppingCartEntities.stream().map(ShoppingCartEntity::getProductId).collect(Collectors.toList());
        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(productIds)
                .stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));
        Map<Long, List<ProductInCartOutput>> productInCartMap = new HashMap<>();

        for(ShoppingCartEntity shoppingCartEntity : shoppingCartEntities) {
            ProductEntity productEntity = productEntityMap.get(shoppingCartEntity.getProductId());

            ProductInCartOutput productInCartOutput = ProductInCartOutput.builder()
                    .shoppingCartId(shoppingCartEntity.getId())
                    .productId(shoppingCartEntity.getProductId())
                    .name(productEntity.getName())
                    .quantity(shoppingCartEntity.getQuantity())
                    .price(shoppingCartEntity.getPrice())
                    .imageUrl(productEntity.getImageUrl())
                    .totalPrice(shoppingCartEntity.getTotalPrice())
                    .build();

            if(productInCartMap.containsKey(shoppingCartEntity.getShopId())) {
                productInCartMap.get(shoppingCartEntity.getShopId()).add(productInCartOutput);
            } else {
                List<ProductInCartOutput> productInCartOutputs = new ArrayList<>();
                productInCartOutputs.add(productInCartOutput);
                productInCartMap.put(shoppingCartEntity.getShopId(), productInCartOutputs);
            }
        }

        Set<Long> shopIds = shoppingCartEntities.stream()
                .map(ShoppingCartEntity::getShopId)
                .collect(Collectors.toSet());

        Map<Long, UserEntity> shopEntityMap = userRepository.findAllByIdIn(shopIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        List<ShoppingCartOutput> shoppingCartOutputs = new ArrayList<>();
        for(Long shopId : shopIds) {
            UserEntity shopEntity = shopEntityMap.get(shopId);
            List<ProductInCartOutput> productInCartOutputs = productInCartMap.get(shopId);
            int totalPrice = productInCartOutputs.stream().mapToInt(ProductInCartOutput::getTotalPrice).sum();
            ShoppingCartOutput shoppingCartOutput = ShoppingCartOutput.builder()
                    .shopId(shopId)
                    .nameShop(shopEntity.getFullName())
                    .productInCartOutputs(productInCartOutputs)
                    .totalPrice(totalPrice)
                    .build();

            shoppingCartOutputs.add(shoppingCartOutput);
        }

        int totalPrice = shoppingCartOutputs.stream().mapToInt(ShoppingCartOutput::getTotalPrice).sum();
        ShoppingCartOrderOutput shoppingCartOrderOutput = ShoppingCartOrderOutput.builder()
                .userId(userId)
                .phoneNumber(shoppingCartOrderInput.getPhoneNumber())
                .address(shoppingCartOrderInput.getAddress())
                .shoppingCartOutputs(shoppingCartOutputs)
                .totalPrice(totalPrice)
                .build();

        return shoppingCartOrderOutput;
    }
}
