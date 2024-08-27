package com.example.eCommerceApp.dto.comment;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CommentOutput {
    private Long id;
    private Long userId;
    private String fullName;
    private String image;
    private LocalDateTime creatAt;
    private Integer rating;
    private String comment;
    private List<String> imageUrlsProduct;
}

