package com.example.eCommerceApp.dto.event;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class MessageEventOutput {
    private Long chatId;
    private Long userId;
    private String fullName;
    private String imageUrl;
    private String message;
    private Boolean isMe;
    private LocalDateTime createdAt;
}