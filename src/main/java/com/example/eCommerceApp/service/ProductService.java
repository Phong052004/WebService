package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.product.*;
import com.example.eCommerceApp.entity.ShoppingCartEntity;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.entity.VoucherEntity;
import com.example.eCommerceApp.entity.product.*;
import com.example.eCommerceApp.helper.StringUtils;
import com.example.eCommerceApp.mapper.product.ProductMapper;
import com.example.eCommerceApp.mapper.product.ProductTemplateMapper;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.ShoppingCartRepository;
import com.example.eCommerceApp.repository.UserRepository;
import com.example.eCommerceApp.repository.VoucherRepository;
import com.example.eCommerceApp.repository.product.*;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ProductService {
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductAttributeValueMapRepository productAttributeValueMapRepository;
    private final CustomRepository customRepository;
    private final ProductTemplateMapper productTemplateMapper;
    private final ProductMapper productMapper;
    private final UserRepository userRepository;
    private final VoucherRepository voucherRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final TemplateAttributeMapRepository templateAttributeMapRepository;

    @Transactional
    public void createProductTemplate(String accessToken,
                                      ProductTemplateInput productTemplateInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = productTemplateMapper
                .getEntityFromInput(productTemplateInput);
        productTemplateEntity.setShopId(shopId);
        productTemplateEntity.setSold(0);
        productTemplateEntity.setAverageRating(0.0);
        productTemplateEntity.setCommentCount(0);
        productTemplateEntity.setLikeCount(0);
        productTemplateRepository.save(productTemplateEntity);

        shopEntity.setTotalProduct(shopEntity.getTotalProduct() + 1);
        userRepository.save(shopEntity);
    }

    @Transactional
    public void createAttribute(String accessToken, Long productTemplateId, List<AttributeInput> attributeInputs) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        for(AttributeInput attributeInput : attributeInputs) {
            if(attributeInput.getExisted().equals(Boolean.FALSE)) {
                AttributeEntity attributeEntity = AttributeEntity.builder()
                        .name(attributeInput.getName())
                        .isOfShop(Boolean.TRUE)
                        .build();
                attributeRepository.save(attributeEntity);

                for(AttributeValueInput attributeValue : attributeInput.getAttributeValues()) {
                    if(attributeValue.getExisted().equals(Boolean.FALSE)) {
                        AttributeValueEntity attributeValueEntity = AttributeValueEntity.builder()
                                .name(attributeValue.getName())
                                .attributeId(attributeEntity.getId())
                                .isOfShop(Boolean.TRUE)
                                .build();

                        attributeValueRepository.save(attributeValueEntity);

                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeEntity.getId())
                                .attributeValueId(attributeValueEntity.getId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    }
                }
            } else {
                for(AttributeValueInput attributeValue : attributeInput.getAttributeValues()) {
                    if(attributeValue.getExisted().equals(Boolean.FALSE)) {
                        AttributeValueEntity attributeValueEntity = AttributeValueEntity.builder()
                                .name(attributeValue.getName())
                                .attributeId(attributeInput.getAttributeId())
                                .isOfShop(Boolean.TRUE)
                                .build();

                        attributeValueRepository.save(attributeValueEntity);

                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeInput.getAttributeId())
                                .attributeValueId(attributeValueEntity.getId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    } else {
                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeInput.getAttributeId())
                                .attributeValueId(attributeValue.getAttributeValueId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    }
                }
            }
        }
    }

    @Transactional
    public void createProducts(String accessToken, List<ProductInput> productInputs, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        templateAttributeMapRepository.deleteAllByProductTemplateId(productTemplateId);

        addProducts(productInputs, productTemplateEntity);
    }

    @Transactional
    public void updateProductTemplate(String accessToken,Long productTemplateId, ProductTemplateInput productTemplateInput) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        productTemplateMapper.updateEntityFromInput(productTemplateEntity, productTemplateInput);
        productTemplateRepository.save(productTemplateEntity);
    }

    @Transactional
    public void updateAttributeAndAttributeValue(String accessToken, Long productTemplateId, List<AttributeInput> attributeInputs) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getAttributeId).collect(Collectors.toSet());
        Set<Long> attributeValueIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getAttributeValueId).collect(Collectors.toSet());

        Map<Long, AttributeValueEntity> attributeValueEntityMap = attributeValueRepository.findAllByIdIn(attributeValueIds)
                .stream().collect(Collectors.toMap(AttributeValueEntity::getId, Function.identity()));

        Map<Long, AttributeInput> attributeEntityMap = new HashMap<>();
        List<Long> attributeIdList = new ArrayList<>();
        for (AttributeInput attributeInput : attributeInputs) {
            if (attributeInput.getExisted().equals(Boolean.FALSE)) {
                AttributeEntity attributeEntity = AttributeEntity.builder()
                        .name(attributeInput.getName())
                        .isOfShop(Boolean.TRUE)
                        .build();
                attributeRepository.save(attributeEntity);

                attributeEntityMap.put(attributeEntity.getId(), attributeInput);
                attributeIdList.add(attributeEntity.getId());
            } else {
                attributeEntityMap.put(attributeInput.getAttributeId(), attributeInput);
                attributeIdList.add(attributeInput.getAttributeId());
            }
        }

        if (attributeIds.size() != attributeInputs.size()) {
            attributeValueRepository.deleteAllByIdInAndIsOfShop(attributeValueIds, Boolean.TRUE);
            for (Long attributeId : attributeIdList) {
                AttributeInput attributeInput = attributeEntityMap.get(attributeId);
                for (AttributeValueInput attributeValueInput : attributeInput.getAttributeValues()) {
                    if(attributeValueInput.getExisted().equals(Boolean.FALSE) ||
                            attributeValueInput.getExisted().equals(Boolean.TRUE) && attributeValueInput.getIsOfShop().equals(Boolean.TRUE)) {
                        AttributeValueEntity attributeValueEntity = AttributeValueEntity.builder()
                                .name(attributeValueInput.getName())
                                .attributeId(attributeId)
                                .isOfShop(Boolean.TRUE)
                                .build();

                        attributeValueRepository.save(attributeValueEntity);

                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeId)
                                .attributeValueId(attributeValueEntity.getId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    } else if (attributeValueInput.getExisted().equals(Boolean.TRUE) && attributeValueInput.getIsOfShop().equals(Boolean.FALSE)) {
                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeId)
                                .attributeValueId(attributeValueInput.getAttributeValueId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    }
                }
            }
        } else {
            for (Long attributeId : attributeIdList) {
                AttributeInput attributeInput = attributeEntityMap.get(attributeId);
                for (AttributeValueInput attributeValueInput : attributeInput.getAttributeValues()) {
                    if (attributeValueInput.getExisted().equals(Boolean.FALSE)) {
                        AttributeValueEntity attributeValueEntity = AttributeValueEntity.builder()
                                .name(attributeValueInput.getName())
                                .attributeId(attributeInput.getAttributeId())
                                .isOfShop(Boolean.TRUE)
                                .build();

                        attributeValueRepository.save(attributeValueEntity);

                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeInput.getAttributeId())
                                .attributeValueId(attributeValueEntity.getId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    } else if (attributeValueInput.getExisted().equals(Boolean.TRUE) && attributeValueInput.getIsOfShop().equals(Boolean.TRUE)) {
                        AttributeValueEntity attributeValueEntity = attributeValueEntityMap.get(attributeValueInput.getAttributeValueId());
                        attributeValueEntity.setName(attributeValueInput.getName());
                        attributeValueRepository.save(attributeValueEntity);
                        attributeValueIds.remove(attributeValueEntity.getId());

                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeInput.getAttributeId())
                                .attributeValueId(attributeValueEntity.getId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    } else if (attributeValueInput.getExisted().equals(Boolean.TRUE) && attributeValueInput.getIsOfShop().equals(Boolean.FALSE)) {
                        TemplateAttributeMap templateAttributeMap = TemplateAttributeMap.builder()
                                .productTemplateId(productTemplateId)
                                .attributeId(attributeInput.getAttributeId())
                                .attributeValueId(attributeValueInput.getAttributeValueId())
                                .build();

                        templateAttributeMapRepository.save(templateAttributeMap);
                    }
                }
            }

            if(!attributeValueIds.isEmpty()) {
                attributeValueRepository.deleteAllByIdInAndIsOfShop(attributeValueIds, Boolean.TRUE);
            }
        }
    }

    @Transactional
    public void updateProductsAfterChangeAttribute(String accessToken,List<ProductInput> productInputs, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<Long> productIds = productRepository.findAllByProductTemplateId(productTemplateId)
                .stream().map(ProductEntity::getId).collect(Collectors.toList());

        templateAttributeMapRepository.deleteAllByProductTemplateId(productTemplateId);
        productAttributeValueMapRepository.deleteAllByProductTemplateId(productTemplateId);
        productRepository.deleteAllByProductTemplateId(productTemplateId);
        shoppingCartRepository.deleteAllByProductIdIn(productIds);
        addProducts(productInputs, productTemplateEntity);
    }

    @Transactional
    public void updateProducts(String accessToken,List<ProductInput> productInputs, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductEntity> productEntities = productRepository.findAllByProductTemplateId(productTemplateId);

        Map<Long, ProductEntity> productEntityMap = productEntities
                .stream().collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        List<Long> productIds = productEntities.stream().map(ProductEntity::getId).collect(Collectors.toList());

        Map<Long, List<ShoppingCartEntity>> shoppingCartEntityMap = shoppingCartRepository.findAllByProductIdIn(productIds)
                .stream().collect(Collectors.groupingBy(ShoppingCartEntity::getProductId));
        for(ProductInput productInput : productInputs) {
            if (productInput.getPrice() < productTemplateEntity.getMinPrice() ||
                    productInput.getPrice() > productTemplateEntity.getMaxPrice()) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
            ProductEntity productEntity = productEntityMap.get(productInput.getProductId());
            productMapper.updateEntityFormInput(productEntity,productInput);
            productRepository.save(productEntity);

            List<ShoppingCartEntity> shoppingCartEntityList = shoppingCartEntityMap.get(productInput.getProductId());
            if(Objects.nonNull(shoppingCartEntityList)) {
                for(ShoppingCartEntity shoppingCartEntity : shoppingCartEntityList) {
                    shoppingCartEntity.setPrice(productInput.getPrice());
                    shoppingCartEntity.setTotalPrice(productInput.getPrice() * shoppingCartEntity.getQuantity());
                    shoppingCartEntity.setNameProduct(productInput.getName());
                    shoppingCartRepository.save(shoppingCartEntity);
                }
            }
        }

        voucherRepository.deleteAllByProductIdIn(productIds);
        voucherRepository.deleteByProductTemplateId(productTemplateId);
    }

    @Transactional(readOnly = true)
    public List<AttributeOutput> getAttributeApp(String accessToken) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIsOfShop(Boolean.FALSE);
        List<AttributeOutput> attributeOutputs = new ArrayList<>();

        for(AttributeEntity attributeEntity : attributeEntities) {
            AttributeOutput attributeOutput = AttributeOutput.builder()
                    .attributeId(attributeEntity.getId())
                    .name(attributeEntity.getName())
                    .existed(Boolean.TRUE)
                    .isOfShop(attributeEntity.getIsOfShop())
                    .build();
            attributeOutputs.add(attributeOutput);
        }

        return attributeOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeOutput> getAttributeProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = productAttributeValueMapEntities.stream()
                .map(ProductAttributeValueMapEntity::getAttributeId).collect(Collectors.toSet());

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIdIn(attributeIds);
        List<AttributeOutput> attributeOutputs = new ArrayList<>();

        for(AttributeEntity attributeEntity : attributeEntities) {
            AttributeOutput attributeOutput = AttributeOutput.builder()
                    .attributeId(attributeEntity.getId())
                    .name(attributeEntity.getName())
                    .isOfShop(attributeEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .build();
            attributeOutputs.add(attributeOutput);
        }
        return attributeOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeValueOutput> getAttributeValueOfApp(String accessToken, Long attributeId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<AttributeValueEntity> attributeValueEntities = attributeValueRepository
                .findAllByAttributeIdAndIsOfShop(attributeId, Boolean.FALSE);
        List<AttributeValueOutput> attributeValueOutputs = new ArrayList<>();
        for(AttributeValueEntity attributeValueEntity : attributeValueEntities) {
            AttributeValueOutput attributeValueOutput = AttributeValueOutput.builder()
                    .attributeValueId(attributeValueEntity.getId())
                    .name(attributeValueEntity.getName())
                    .isOfShop(attributeValueEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .build();

            attributeValueOutputs.add(attributeValueOutput);
        }
        return attributeValueOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeValueOutput> getAttributeValueOfProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeValueIds = productAttributeValueMapEntities.stream()
                        .map(ProductAttributeValueMapEntity::getAttributeValueId).collect(Collectors.toSet());

        List<AttributeValueEntity> attributeValueEntities = attributeValueRepository.findAllByIdIn(attributeValueIds);

        List<AttributeValueOutput> attributeValueOutputs = new ArrayList<>();

        for(AttributeValueEntity attributeValueEntity : attributeValueEntities) {
            AttributeValueOutput attributeValueOutput = AttributeValueOutput.builder()
                    .attributeValueId(attributeValueEntity.getId())
                    .name(attributeValueEntity.getName())
                    .isOfShop(attributeValueEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .build();

            attributeValueOutputs.add(attributeValueOutput);
        }
        return attributeValueOutputs;
    }

    @Transactional(readOnly = true)
    public List<AttributeOutput> getAttributeAndAttributeValueOfProduct(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<TemplateAttributeMap> templateAttributes = templateAttributeMapRepository
                .findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = templateAttributes.stream()
                .map(TemplateAttributeMap::getAttributeId).collect(Collectors.toSet());

        Set<Long> attributeValueIds = templateAttributes.stream()
                .map(TemplateAttributeMap::getAttributeValueId).collect(Collectors.toSet());

        Map<Long, List<Long>> attributeMap = templateAttributes.stream().collect(
                Collectors.groupingBy(
                        TemplateAttributeMap::getAttributeId,
                        Collectors.mapping(TemplateAttributeMap::getAttributeValueId, Collectors.toList())
                )
        );

        List<AttributeEntity> attributeEntities = attributeRepository.findAllByIdIn(attributeIds);
        Map<Long, AttributeValueEntity> attributeValueEntityMap = attributeValueRepository
                .findAllByIdIn(attributeValueIds).stream().collect(
                        Collectors.toMap(AttributeValueEntity::getId, Function.identity())
                );

        List<AttributeOutput> attributeOutputs = new ArrayList<>();
        for(AttributeEntity attributeEntity : attributeEntities) {
            List<Long> attributeValueIdList = attributeMap.get(attributeEntity.getId());
            List<AttributeValueOutput> attributeValueOutputs = new ArrayList<>();
            for (Long attributeValueId : attributeValueIdList) {
                AttributeValueEntity attributeValueEntity = attributeValueEntityMap.get(attributeValueId);
                AttributeValueOutput attributeValueOutput = AttributeValueOutput.builder()
                        .attributeValueId(attributeValueId)
                        .name(attributeValueEntity.getName())
                        .isOfShop(attributeValueEntity.getIsOfShop())
                        .existed(Boolean.TRUE)
                        .build();

                attributeValueOutputs.add(attributeValueOutput);
            }
            AttributeOutput attributeOutput = AttributeOutput.builder()
                    .name(attributeEntity.getName())
                    .attributeId(attributeEntity.getId())
                    .isOfShop(attributeEntity.getIsOfShop())
                    .existed(Boolean.TRUE)
                    .attributeValueOutputs(attributeValueOutputs)
                    .build();

            attributeOutputs.add(attributeOutput);
        }
        return attributeOutputs;
    }

    @Transactional(readOnly = true)
    public Page<ProductsTemplateOutput> getProductsTemplate(Long shopId, Pageable pageable) {
        Page<ProductTemplateEntity> productTemplateEntities = productTemplateRepository.findAllByShopId(shopId,pageable);
        return getProductsTemplate(productTemplateEntities);
    }

    @Transactional(readOnly = true)
    public List<ProductOutPut> getProducts(Long productTemplateId) {
        List<ProductEntity> productEntities = productRepository.findAllByProductTemplateId(productTemplateId);

        List<Long> productIds = productEntities.stream().map(ProductEntity::getId).collect(Collectors.toList());

        Map<Long, VoucherEntity> voucherEntityMap = voucherRepository.findAllByProductIdIn(productIds)
                .stream().collect(Collectors.toMap(VoucherEntity::getProductId, Function.identity()));

        List<ProductOutPut> productOutPuts = new ArrayList<>();
        for(ProductEntity productEntity : productEntities) {
            VoucherEntity voucherEntity = voucherEntityMap.get(productEntity.getId());

            ProductOutPut productOutPut = ProductOutPut.builder()
                    .productId(productEntity.getId())
                    .name(productEntity.getName())
                    .price(productEntity.getPrice())
                    .quantity(productEntity.getQuantity())
                    .imageUrl(productEntity.getImageUrl())
                    .existed(Boolean.TRUE)
                    .build();
            if(Objects.nonNull(voucherEntity)) {
                productOutPut.setDiscountedPrice(voucherEntity.getDiscountedPrice());
                productOutPut.setSaleOffInteger(voucherEntity.getSaleOffInteger());
                productOutPut.setSaleOffFloat(voucherEntity.getSaleOffFloat());
            }

            productOutPuts.add(productOutPut);
        }

        return productOutPuts;
    }

    @Transactional(readOnly = true)
    public ProductOutPut getProductByAttributeValues(List<Long> attributeValueIds, Long productTemplateId) {
        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities = productAttributeValueMapRepository
                .findAllByAttributeValueIdInAndProductTemplateId(attributeValueIds, productTemplateId);

        if(productAttributeValueMapEntities.isEmpty()) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        Map<Long, List<Long>> productEntityMap = productAttributeValueMapEntities.stream()
                .collect(Collectors.groupingBy(
                        ProductAttributeValueMapEntity::getProductId,
                        Collectors.mapping(ProductAttributeValueMapEntity::getAttributeValueId, Collectors.toList())
                        )
                );

        long productId = 0;
        for(ProductAttributeValueMapEntity productAttributeValueMapEntity : productAttributeValueMapEntities) {
            if(productEntityMap.get(productAttributeValueMapEntity.getProductId()).size() == attributeValueIds.size()) {
                productId = productAttributeValueMapEntity.getProductId();
                break;
            }
        }

        if(productId == 0) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductEntity productEntity = customRepository.getProductBy(productId);
        VoucherEntity voucherEntity = voucherRepository.findByProductId(productId);

        ProductOutPut productOutPut = ProductOutPut.builder()
                .name(productEntity.getName())
                .productId(productId)
                .price(productEntity.getPrice())
                .quantity(productEntity.getQuantity())
                .imageUrl(productEntity.getImageUrl())
                .existed(Boolean.TRUE)
                .build();

        if(Objects.nonNull(voucherEntity)) {
            productOutPut.setDiscountedPrice(voucherEntity.getDiscountedPrice());
            productOutPut.setSaleOffInteger(voucherEntity.getSaleOffInteger());
            productOutPut.setSaleOffFloat(voucherEntity.getSaleOffFloat());
        }

        return productOutPut;
    }

    @Transactional
    public void deleteProductTemplate(String accessToken, Long productTemplateId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        if(Boolean.FALSE.equals(shopEntity.getIsShop())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductAttributeValueMapEntity> productAttributeValueMapEntities =
                productAttributeValueMapRepository.findAllByProductTemplateId(productTemplateId);

        Set<Long> attributeIds = productAttributeValueMapEntities.stream()
                        .map(ProductAttributeValueMapEntity::getAttributeId).collect(Collectors.toSet());

        attributeRepository.deleteAllByIdInAndIsOfShop(attributeIds, Boolean.TRUE);

        Set<Long> attributeValueIds = productAttributeValueMapEntities
                .stream().map(ProductAttributeValueMapEntity::getAttributeValueId).collect(Collectors.toSet());

        attributeValueRepository.deleteAllByIdInAndIsOfShop(attributeValueIds, Boolean.TRUE);

        productTemplateRepository.deleteById(productTemplateId);
        productRepository.deleteAllByProductTemplateId(productTemplateId);
        productAttributeValueMapRepository.deleteAllByProductTemplateId(productTemplateId);
    }

    public void addProducts(List<ProductInput> productInputs, ProductTemplateEntity productTemplateEntity) {
        Set<Long> attributeValueIds = productInputs.stream()
                .flatMap(productInput -> productInput.getAttributeValueIds().stream())
                .collect(Collectors.toSet());

        Map<Long, Long> attributeValueIdMap = attributeValueRepository.findAllByIdIn(attributeValueIds)
                .stream().collect(Collectors.toMap(AttributeValueEntity::getId, AttributeValueEntity::getAttributeId));

        List<String> imageUrls = new ArrayList<>();
        for (ProductInput productInput : productInputs) {
            if (productInput.getPrice() < productTemplateEntity.getMinPrice() ||
                    productInput.getPrice() > productTemplateEntity.getMaxPrice()) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
            imageUrls.add(productInput.getImageUrl());

            ProductEntity productEntity = productMapper.getEntityFromInput(productInput);
            productEntity.setProductTemplateId(productTemplateEntity.getId());
            productRepository.save(productEntity);

            if (!productInput.getAttributeValueIds().isEmpty()) {
                for (Long attributeValueId : productInput.getAttributeValueIds()) {
                    ProductAttributeValueMapEntity productAttributeValueMapEntity = ProductAttributeValueMapEntity.builder()
                            .attributeValueId(attributeValueId)
                            .attributeId(attributeValueIdMap.get(attributeValueId))
                            .productId(productEntity.getId())
                            .productTemplateId(productTemplateEntity.getId())
                            .build();

                    productAttributeValueMapRepository.save(productAttributeValueMapEntity);
                }
            }
        }
        productTemplateEntity.setImages(StringUtils.getStringFromList(imageUrls));
        productTemplateEntity.setAvatarImage(imageUrls.get(0));
        productTemplateRepository.save(productTemplateEntity);
    }

    @Transactional
    public Page<ProductsTemplateOutput> searchProductsTemplateBy(String search, Pageable pageable) {
        Page<ProductTemplateEntity> productTemplateEntities = productTemplateRepository
                .searchProductTemplateEntitiesByString(search, pageable);

        if (Objects.isNull(productTemplateEntities) || productTemplateEntities.isEmpty()) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        return getProductsTemplate(productTemplateEntities);
    }

    public Page<ProductsTemplateOutput> getProductsTemplate(Page<ProductTemplateEntity> productTemplateEntities) {
        if(Objects.isNull(productTemplateEntities) || productTemplateEntities.isEmpty()) {
            return Page.empty();
        }

        List<Long> productTemplateIds = productTemplateEntities
                .stream().map(ProductTemplateEntity::getId).collect(Collectors.toList());

        Map<Long, VoucherEntity> voucherEntityMap = voucherRepository.findAllByProductTemplateIdIn(productTemplateIds)
                .stream().collect(Collectors.toMap(VoucherEntity::getProductTemplateId, Function.identity()));

        return productTemplateEntities.map(
                productTemplateEntity -> {
                    VoucherEntity voucherEntity = voucherEntityMap.get(productTemplateEntity.getId());
                    ProductsTemplateOutput productsTemplateOutput = ProductsTemplateOutput.builder()
                            .productTemplateId(productTemplateEntity.getId())
                            .name(productTemplateEntity.getName())
                            .minPrice(productTemplateEntity.getMinPrice())
                            .maxPrice(productTemplateEntity.getMaxPrice())
                            .description(productTemplateEntity.getDescription())
                            .quantity(productTemplateEntity.getQuantity())
                            .avatarImage(productTemplateEntity.getAvatarImage())
                            .soldCount(productTemplateEntity.getSold())
                            .averageRate(productTemplateEntity.getAverageRating())
                            .build();

                    if(Objects.nonNull(voucherEntity)) {
                        productsTemplateOutput.setDiscountedPrice(voucherEntity.getDiscountedPrice());
                        productsTemplateOutput.setSaleOffInteger(voucherEntity.getSaleOffInteger());
                        productsTemplateOutput.setSaleOffFloat(voucherEntity.getSaleOffFloat());
                    }
                    return productsTemplateOutput;
                }
        );
    }
}
