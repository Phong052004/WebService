package com.example.eCommerceApp.entity.product;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_product_template")
@Builder
public class ProductTemplateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long shopId;
    private String name;
    private Integer minPrice;
    private Integer maxPrice;
    private Integer quantity;
    private Integer sold;
    private String description;
    private String images;
    private String avatarImage;
    private String sizeConvertImage;
    private Integer commentCount;
    private Double averageRating;
    private Integer likeCount;
    private Long subCategoryId;
}
