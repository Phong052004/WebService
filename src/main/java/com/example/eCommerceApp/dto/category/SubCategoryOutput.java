package com.example.eCommerceApp.dto.category;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class SubCategoryOutput {
    private Long id;
    private Long categoryId;
    private Long subCategoryId;
    private String name;
}
