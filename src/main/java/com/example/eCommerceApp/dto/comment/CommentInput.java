package com.example.eCommerceApp.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CommentInput {
    private String comment;
    private List<String> imageUrls;
    private Integer rating;
}