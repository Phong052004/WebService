package com.example.eCommerceApp.dto.message;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageOutput {
    private Long id;
    private Long userId;
    private String message;
    private String fullName;
    private String imageUrl;
    private Boolean isMe;
    private LocalDateTime createdAt;
}

