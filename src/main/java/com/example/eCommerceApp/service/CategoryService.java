package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.category.CategoryOutput;
import com.example.eCommerceApp.dto.category.SubCategoryOutput;
import com.example.eCommerceApp.dto.product.ProductsTemplateOutput;
import com.example.eCommerceApp.entity.category.CategoryEntity;
import com.example.eCommerceApp.entity.category.SubCategoryEntity;
import com.example.eCommerceApp.entity.product.ProductTemplateEntity;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.category.CategoryRepository;
import com.example.eCommerceApp.repository.category.SubCategoryRepository;
import com.example.eCommerceApp.repository.product.ProductTemplateRepository;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final CustomRepository customRepository;
    private final ProductTemplateRepository productTemplateRepository;
    private final ProductService productService;

    @Transactional
    public List<CategoryOutput> getAllCategory() {
        List<CategoryEntity> categoryEntities = categoryRepository.getAllCategory();

        List<CategoryOutput> categoryOutputs = new ArrayList<>();
        for(CategoryEntity category : categoryEntities) {
            CategoryOutput categoryOutput = CategoryOutput.builder()
                    .categoryId(category.getId())
                    .name(category.getName())
                    .build();

            categoryOutputs.add(categoryOutput);
        }

        return categoryOutputs;
    }

    @Transactional
    public List<SubCategoryOutput> getSubCategories(Long categoryId) {
        List<SubCategoryEntity> subCategoryEntities = subCategoryRepository.findAllByCategoryId(categoryId);

        List<SubCategoryOutput> subCategoryOutputs = new ArrayList<>();
        for(SubCategoryEntity subCategoryEntity : subCategoryEntities) {
            SubCategoryOutput subCategoryOutput = SubCategoryOutput.builder()
                    .categoryId(categoryId)
                    .subCategoryId(subCategoryEntity.getCategoryId())
                    .name(subCategoryEntity.getName())
                    .build();

            subCategoryOutputs.add(subCategoryOutput);
        }

        return subCategoryOutputs;
    }

    @Transactional
    public void addCategoryForProduct(String accessToken, Long productTemplateId, Long subCategoryId) {
        Long shopId = TokenHelper.getUserIdFromToken(accessToken);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);

        if(!shopId.equals(productTemplateEntity.getShopId())) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        productTemplateEntity.setSubCategoryId(subCategoryId);
        productTemplateRepository.save(productTemplateEntity);
    }

    @Transactional(readOnly = true)
    public Page<ProductsTemplateOutput> getProductsTemplateByCategory(Long categoryId, Pageable pageable) {
        List<Long> subCategoryIds = subCategoryRepository.findAllByCategoryId(categoryId)
                .stream().map(SubCategoryEntity::getId).collect(Collectors.toList());

        Page<ProductTemplateEntity> productTemplateEntities = productTemplateRepository
                .findAllBySubCategoryIdIn(subCategoryIds, pageable);

        return productService.getProductsTemplate(productTemplateEntities);
    }

    @Transactional(readOnly = true)
    public Page<ProductsTemplateOutput> getProductsTemplateBySubCategory(Long subCategoryId, Pageable pageable) {
        Page<ProductTemplateEntity> productTemplateEntities = productTemplateRepository.findAllBySubCategoryId(
                subCategoryId, pageable
        );

        return productService.getProductsTemplate(productTemplateEntities);
    }
}
