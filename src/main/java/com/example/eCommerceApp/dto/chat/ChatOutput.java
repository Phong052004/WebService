package com.example.eCommerceApp.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ChatOutput {
    private Long id;
    private String name;
    private String imageUrl;
    private String newestMessage;
    private Boolean isMe;
    private LocalDateTime newestChatTime;
    private Integer messageCount;
}
