package com.example.eCommerceApp.service;

import com.example.eCommerceApp.base.filter.Filter;
import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.shoppingorder.*;
import com.example.eCommerceApp.entity.ShoppingCartEntity;
import com.example.eCommerceApp.entity.VoucherEntity;
import com.example.eCommerceApp.entity.product.ProductEntity;
import com.example.eCommerceApp.entity.product.ProductTemplateEntity;
import com.example.eCommerceApp.entity.shoppingorder.ProductOrderMapEntity;
import com.example.eCommerceApp.entity.shoppingorder.ShoppingOrderEntity;
import com.example.eCommerceApp.mapper.shoppingorder.ProductOrderMapMapper;
import com.example.eCommerceApp.mapper.shoppingorder.ShoppingOrderMapper;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.ShoppingCartRepository;
import com.example.eCommerceApp.repository.VoucherRepository;
import com.example.eCommerceApp.repository.product.ProductRepository;
import com.example.eCommerceApp.repository.product.ProductTemplateRepository;
import com.example.eCommerceApp.repository.shoppingorder.ProductOrderMapRepository;
import com.example.eCommerceApp.repository.shoppingorder.ShoppingOrderRepository;
import com.example.eCommerceApp.token.TokenHelper;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ShoppingOrderService {
    private final ShoppingOrderRepository shoppingOrderRepository;
    private final ProductOrderMapRepository productOrderMapRepository;
    private final ShoppingOrderMapper shoppingOrderMapper;
    private final ProductOrderMapMapper productOrderMapMapper;
    private final CustomRepository customRepository;
    private final ProductRepository productRepository;
    private final VoucherRepository voucherRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final EntityManager entityManager;
    private final ProductTemplateRepository productTemplateRepository;

    @Transactional
    public void orderProducts(String accessToken, ShoppingOrderInput shoppingOrderInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        LocalDateTime now = LocalDateTime.now();

        List<Long> productIds = shoppingOrderInput.getProductsInputs().stream()
                .flatMap(order -> order.getProductOrderInputs().stream())
                .map(ProductOrderInput::getProductId)
                .collect(Collectors.toList());

        List<ProductEntity> productEntities = productRepository.findAllByIdIn(productIds);

        for(ProductEntity productEntity : productEntities) {
            if(productEntity.getQuantity() <= 0) {
                throw new RuntimeException(Common.ACTION_FAIL);
            }
        }

        List<Long> shoppingCartIds = new ArrayList<>();

        for(ProductsInput productsInput : shoppingOrderInput.getProductsInputs()) {
            ShoppingOrderEntity shoppingOrderEntity = shoppingOrderMapper.getEntityFromInput(shoppingOrderInput);
            shoppingOrderEntity.setNameShop(productsInput.getNameShop());
            shoppingOrderEntity.setShopId(productsInput.getShopId());
            shoppingOrderEntity.setSaleOffShop(productsInput.getSaleOffShop());
            shoppingOrderEntity.setShippingPrice(productsInput.getShippingPrice());
            shoppingOrderEntity.setCreateAt(now);
            shoppingOrderEntity.setState(Common.WAIT_FOR_PAY);
            shoppingOrderEntity.setUserId(userId);
            shoppingOrderRepository.save(shoppingOrderEntity);

            for(ProductOrderInput productOrderInput : productsInput.getProductOrderInputs()) {
                ProductOrderMapEntity productOrderMapEntity = productOrderMapMapper.getEntityFromInput(productOrderInput);
                productOrderMapEntity.setShoppingOrderId(shoppingOrderEntity.getId());
                productOrderMapEntity.setIsCheck(Boolean.FALSE);
                productOrderMapRepository.save(productOrderMapEntity);

                shoppingCartIds.add(productOrderInput.getShoppingCartId());
            }
        }

        shoppingCartRepository.deleteAllByIdIn(shoppingCartIds);
    }

    @Transactional
    public void updateQuantityProductAfterOrder() {
        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository.findAllByIsCheck(Boolean.FALSE);

        Set<Long> productIds = productOrderMapEntities.stream()
                .map(ProductOrderMapEntity::getProductId).collect(Collectors.toSet());

        List<ProductEntity> productEntities = productRepository.findAllByIdIn(productIds);

        Map<Long, ProductEntity> productEntityMap = productEntities.stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        Set<Long> productTemplateIds = productEntities.stream()
                .map(ProductEntity::getProductTemplateId).collect(Collectors.toSet());

        Map<Long, ProductTemplateEntity> productTemplateEntityMap = productTemplateRepository.findAllByIdIn(productTemplateIds)
                .stream().collect(Collectors.toMap(ProductTemplateEntity::getId, Function.identity()));

        Map<Long, Integer> quantitySoldMap = productOrderMapEntities.stream()
                .collect(Collectors.groupingBy(
                        ProductOrderMapEntity::getProductId,
                        Collectors.summingInt(ProductOrderMapEntity::getQuantityOrder)
                        )
                );

        for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntities) {
            int quantitySold = quantitySoldMap.get(productOrderMapEntity.getProductId());
            ProductEntity productEntity = productEntityMap.get(productOrderMapEntity.getProductId());
            ProductTemplateEntity productTemplateEntity = productTemplateEntityMap.get(productEntity.getProductTemplateId());

            productEntity.setQuantity(productEntity.getQuantity() - quantitySold);
            productTemplateEntity.setQuantity(productTemplateEntity.getQuantity() - quantitySold);
            productTemplateEntity.setSold(productTemplateEntity.getSold() + quantitySold);

            productRepository.save(productEntity);
            productTemplateRepository.save(productTemplateEntity);

            productOrderMapEntity.setIsCheck(Boolean.TRUE);
            productOrderMapRepository.save(productOrderMapEntity);
        }
    }

    @Transactional(readOnly = true)
    public Page<ShoppingOrderOutput> getAllProductOrder(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<ShoppingOrderEntity> shoppingOrderEntities = Filter.builder(ShoppingOrderEntity.class, entityManager)
                .search()
                .isEqual("userId", userId)
                .orderBy("createAt", Common.DESC)
                .getPage(pageable);
        List<Long> shoppingOrderIds = shoppingOrderEntities
                .stream().map(ShoppingOrderEntity::getId).collect(Collectors.toList());
        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByShoppingOrderIdIn(shoppingOrderIds);


        Map<Long, List<ProductOrderMapEntity>> productOrderMap = productOrderMapEntities.stream()
                .collect(Collectors.groupingBy(ProductOrderMapEntity::getShoppingOrderId));

        return shoppingOrderEntities.map(
                shoppingOrderEntity -> {
                    ShoppingOrderOutput shoppingOrderOutput = shoppingOrderMapper.getOutputFromEntity(shoppingOrderEntity);
                    List<ProductOrderMapEntity> productOrderMapEntityList = productOrderMap.get(shoppingOrderEntity.getId());

                    List<ProductOrderInput> productOrderOutputs = new ArrayList<>();
                    for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntityList) {
                        ProductOrderInput productOrderOutput = productOrderMapMapper.getOutputFromEntity(productOrderMapEntity);
                        productOrderOutputs.add(productOrderOutput);
                    }

                    shoppingOrderOutput.setProductOrderInputs(productOrderOutputs);
                    return shoppingOrderOutput;
                }
        );
    }

    @Transactional(readOnly = true)
    public Page<ShoppingOrderOutput> getProductsByState(String accessToken, Pageable pageable, String state) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        Page<ShoppingOrderEntity> shoppingOrderEntities = Filter.builder(ShoppingOrderEntity.class, entityManager)
                .search()
                .isEqual("userId", userId)
                .isEqual("state", state)
                .orderBy("createAt", Common.DESC)
                .getPage(pageable);
        List<Long> shoppingOrderIds = shoppingOrderEntities
                .stream().map(ShoppingOrderEntity::getId).collect(Collectors.toList());
        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByShoppingOrderIdIn(shoppingOrderIds);

        Map<Long, List<ProductOrderMapEntity>> productOrderMap = productOrderMapEntities.stream()
                .collect(Collectors.groupingBy(ProductOrderMapEntity::getShoppingOrderId));

        return shoppingOrderEntities.map(
                shoppingOrderEntity -> {
                    ShoppingOrderOutput shoppingOrderOutput = shoppingOrderMapper.getOutputFromEntity(shoppingOrderEntity);
                    List<ProductOrderMapEntity> productOrderMapEntityList = productOrderMap.get(shoppingOrderEntity.getId());

                    List<ProductOrderInput> productOrderOutputs = new ArrayList<>();
                    for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntityList) {
                        ProductOrderInput productOrderOutput = productOrderMapMapper.getOutputFromEntity(productOrderMapEntity);
                        productOrderOutputs.add(productOrderOutput);
                    }

                    shoppingOrderOutput.setProductOrderInputs(productOrderOutputs);
                    return shoppingOrderOutput;
                }
        );
    }

    @Transactional(readOnly = true)
    public OrderDescriptionOutput getDetailsOrder(String accessToken, Long shoppingOrderId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ShoppingOrderEntity shoppingOrderEntity = customRepository.getShoppingOrderBy(shoppingOrderId);
        if(!userId.equals(shoppingOrderEntity.getUserId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository
                .findAllByShoppingOrderId(shoppingOrderId);
        List<ProductOrderInput> productOrderOutputs = new ArrayList<>();
        for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntities) {
            ProductOrderInput productOrderOutput = productOrderMapMapper.getOutputFromEntity(productOrderMapEntity);
            productOrderOutputs.add(productOrderOutput);
        }

        return OrderDescriptionOutput.builder()
                .fullName(shoppingOrderEntity.getFullName())
                .address(shoppingOrderEntity.getAddress())
                .telephoneNumber(shoppingOrderEntity.getPhoneNumber())
                .productOrderOutputs(productOrderOutputs)
                .state(shoppingOrderEntity.getState())
                .shippingPrice(shoppingOrderEntity.getShippingPrice())
                .saleOffShop(shoppingOrderEntity.getSaleOffShop())
                .totalPrice(shoppingOrderEntity.getTotalPrice())
                .build();
    }

    @Transactional
    public void repurchase(String accessToken, Long shoppingOrderId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ShoppingOrderEntity shoppingOrderEntity = customRepository.getShoppingOrderBy(shoppingOrderId);
        if(!shoppingOrderEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        LocalDateTime now = LocalDateTime.now();

        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository.findAllByShoppingOrderId(shoppingOrderId);
        List<Long> productIds = productOrderMapEntities
                .stream().map(ProductOrderMapEntity::getProductId).collect(Collectors.toList());

        List<ProductEntity> productEntities = productRepository.findAllByIdIn(productIds);
        Map<Long, ProductEntity> productEntityMap = productEntities.stream().collect(
                Collectors.toMap(ProductEntity::getId, Function.identity())
        );
        Map<Long, VoucherEntity> voucherEntityMap = voucherRepository.findAllByProductIdIn(productIds)
                .stream().collect(Collectors.toMap(VoucherEntity::getProductId, Function.identity()));

        Map<Long, ShoppingCartEntity> shoppingCartEntityMapExist = shoppingCartRepository.findAllByProductIdIn(productIds)
                .stream().collect(Collectors.toMap(ShoppingCartEntity::getProductId, Function.identity()));
        for(ProductOrderMapEntity productOrderMapEntity : productOrderMapEntities) {
            VoucherEntity voucherEntity = voucherEntityMap.get(productOrderMapEntity.getProductId());
            ShoppingCartEntity shoppingCartEntityExisted = shoppingCartEntityMapExist.get(productOrderMapEntity.getProductId());
            ProductEntity productEntity = productEntityMap.get(productOrderMapEntity.getProductId());

            if(Objects.isNull(shoppingCartEntityExisted)) {
                ShoppingCartEntity shoppingCartEntity = ShoppingCartEntity.builder()
                        .shopId(shoppingOrderEntity.getShopId())
                        .nameProduct(productEntity.getName())
                        .image(productEntity.getImageUrl())
                        .productId(productOrderMapEntity.getProductId())
                        .userId(userId)
                        .quantity(productOrderMapEntity.getQuantityOrder())
                        .createAt(now)
                        .build();
                if(Objects.isNull(voucherEntity)) {
                    shoppingCartEntity.setPrice(productEntity.getPrice());
                    shoppingCartEntity.setTotalPrice(productEntity.getPrice() * productOrderMapEntity.getQuantityOrder());
                } else {
                    shoppingCartEntity.setPrice(voucherEntity.getDiscountedPrice());
                    shoppingCartEntity.setTotalPrice(voucherEntity.getDiscountedPrice() * productOrderMapEntity.getQuantityOrder());
                }
                shoppingCartRepository.save(shoppingCartEntity);
            } else {
                int quantity = shoppingCartEntityExisted.getQuantity() + productOrderMapEntity.getQuantityOrder();
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
    }

    @Transactional
    public void cancelOrder(String accessToken, Long orderId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ShoppingOrderEntity shoppingOrderEntity = customRepository.getShoppingOrderBy(orderId);
        if(!shoppingOrderEntity.getUserId().equals(userId)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        if(!shoppingOrderEntity.getState().equals(Common.WAIT_FOR_PAY)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        shoppingOrderEntity.setState(Common.CANCELLED);
        shoppingOrderRepository.save(shoppingOrderEntity);

        List<ProductOrderMapEntity> productOrderMapEntities = productOrderMapRepository.findAllByShoppingOrderId(orderId);

        Map<Long, ProductOrderMapEntity> productOrderMapEntityMap = productOrderMapEntities
                .stream().collect(Collectors.toMap(ProductOrderMapEntity::getProductId, Function.identity()));

        List<Long> productIds = productOrderMapEntities
                .stream().map(ProductOrderMapEntity::getProductId).collect(Collectors.toList());

        List<ProductEntity> productEntities = productRepository.findAllByIdIn(productIds);

        Set<Long> productTemplateIds = productEntities.stream().map(ProductEntity::getProductTemplateId).collect(Collectors.toSet());

        Map<Long, ProductTemplateEntity> productTemplateEntityMap = productTemplateRepository.findAllByIdIn(productTemplateIds)
                .stream().collect(Collectors.toMap(ProductTemplateEntity::getId, Function.identity()));

        for(ProductEntity productEntity : productEntities) {
            ProductOrderMapEntity productOrderMapEntity = productOrderMapEntityMap.get(productEntity.getId());
            ProductTemplateEntity productTemplateEntity = productTemplateEntityMap.get(productEntity.getProductTemplateId());

            productEntity.setQuantity(productEntity.getQuantity() + productOrderMapEntity.getQuantityOrder());
            productTemplateEntity.setQuantity(productTemplateEntity.getQuantity() + productOrderMapEntity.getQuantityOrder());

            productRepository.save(productEntity);
            productTemplateRepository.save(productTemplateEntity);
        }
    }
}
