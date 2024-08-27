package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.category.CategoryOutput;
import com.example.eCommerceApp.dto.category.SubCategoryOutput;
import com.example.eCommerceApp.dto.product.ProductsTemplateOutput;
import com.example.eCommerceApp.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/category")
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Lấy ra danh mục")
    @GetMapping("/all")
    public List<CategoryOutput> getAllCategory() {
        return categoryService.getAllCategory();
    }

    @Operation(summary = "Lấy ra danh mục con")
    @GetMapping("/sub")
    public List<SubCategoryOutput> getSubCategories(@RequestParam Long categoryId) {
        return categoryService.getSubCategories(categoryId);
    }

    @Operation(summary = "Thêm danh mục cho sản phẩm")
    @PostMapping("/add-product")
    public void addCategoryForProduct(@RequestHeader("Authorization") String accessToken,
                                      @RequestParam Long productTemplateId,
                                      @RequestParam Long subCategoryId) {
        categoryService.addCategoryForProduct(accessToken, productTemplateId, subCategoryId);
    }

    @Operation(summary = "Thay đổi danh mục cho sản phẩm")
    @PostMapping("/change-product")
    public void changeCategoryForProduct(@RequestHeader("Authorization") String accessToken,
                                      @RequestParam Long productTemplateId,
                                      @RequestParam Long subCategoryId) {
        categoryService.addCategoryForProduct(accessToken, productTemplateId, subCategoryId);
    }

    @Operation(summary = "Lấy sản phẩm theo danh mục cha")
    @GetMapping("/get-products")
    public Page<ProductsTemplateOutput> getProductsTemplateByCategory(@RequestParam Long categoryId,
                                                                      @ParameterObject Pageable pageable) {
        return categoryService.getProductsTemplateByCategory(categoryId, pageable);
    }

    @Operation(summary = "Lấy sản phẩm theo danh mục con")
    @GetMapping("/get-products-sub")
    public Page<ProductsTemplateOutput> getProductsTemplateBySubCategory(@RequestParam Long subCategoryId,
                                                                         @ParameterObject Pageable pageable) {
        return categoryService.getProductsTemplateBySubCategory(subCategoryId, pageable);
    }
}
