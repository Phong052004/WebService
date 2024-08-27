package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.chat.ChatOutput;
import com.example.eCommerceApp.dto.message.MessageOutput;
import com.example.eCommerceApp.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/chat")
public class ChatController {
    private final ChatService chatService;

    @Operation(summary = "Lấy danh sách tin nhắn trong cuộc trò chuyện")
    @GetMapping("/messages")
    public Page<MessageOutput> getMessages(@RequestHeader("Authorization") String accessToken,
                                           @RequestParam Long chatId,
                                           @ParameterObject Pageable pageable){
        return chatService.getMessages(accessToken, chatId, pageable);
    }

    @Operation(summary = "Lấy danh sách các cuộc chat của user")
    @GetMapping
    public Page<ChatOutput> getChatList(@RequestParam(required = false) String search,
                                        @RequestHeader("Authorization") String accessToken,
                                        @ParameterObject Pageable pageable) {
        return chatService.getChatList(search, accessToken, pageable);
    }
}

