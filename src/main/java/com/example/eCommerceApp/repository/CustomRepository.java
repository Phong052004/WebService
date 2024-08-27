package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.entity.ChatEntity;
import com.example.eCommerceApp.entity.ShoppingCartEntity;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.entity.VoucherEntity;
import com.example.eCommerceApp.entity.product.*;
import com.example.eCommerceApp.entity.shoppingorder.ShoppingOrderEntity;
import com.example.eCommerceApp.repository.product.*;
import com.example.eCommerceApp.repository.shoppingorder.ShoppingOrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class CustomRepository {
    private final UserRepository userRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductRepository productRepository;
    private final AttributeRepository attributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductAttributeValueMapRepository productAttributeValueMapRepository;
    private final VoucherRepository voucherRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingOrderRepository shoppingOrderRepository;
    private final ChatRepository chatRepository;

    public UserEntity getUserBy(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductTemplateEntity getProductTemplateBy(Long productTemplateId) {
        return productTemplateRepository.findById(productTemplateId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductEntity getProductBy(Long productId) {
        return productRepository.findById(productId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public AttributeEntity getAttributeBy(Long attributeId) {
        return attributeRepository.findById(attributeId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public AttributeValueEntity getAttributeValueBy(Long attributeValueId) {
        return attributeValueRepository.findById(attributeValueId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ProductAttributeValueMapEntity getProductAttributeValueBy(Long productAttributeValueId) {
        return productAttributeValueMapRepository.findById(productAttributeValueId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public VoucherEntity getVoucherBy(Long voucherId) {
        return voucherRepository.findById(voucherId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ShoppingCartEntity getShoppingCartBy(Long shoppingCartId) {
        return shoppingCartRepository.findById(shoppingCartId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ShoppingOrderEntity getShoppingOrderBy(Long shoppingOrderId) {
        return shoppingOrderRepository.findById(shoppingOrderId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }

    public ChatEntity getChatEntityBy(Long chatId) {
        return chatRepository.findById(chatId).orElseThrow(
                () -> new RuntimeException(Common.ACTION_FAIL)
        );
    }
}
