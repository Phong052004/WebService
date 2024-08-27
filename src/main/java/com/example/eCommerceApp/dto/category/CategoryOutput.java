package com.example.eCommerceApp.dto.category;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CategoryOutput {
    private Long id;
    private Long categoryId;
    private String name;
}
