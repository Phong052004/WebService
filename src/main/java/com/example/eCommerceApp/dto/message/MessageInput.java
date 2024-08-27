package com.example.eCommerceApp.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@Getter
public class MessageInput {
    private Long chatId;
    private String message;
}

