package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.message.MessageInput;
import com.example.eCommerceApp.service.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/message")
public class MessageController {
    private final MessageService messageService;

    @Operation(summary = "Gửi tin nhắn")
    @PostMapping("/send")
    public String sendMessage(@RequestBody MessageInput messageInput,
                              @RequestHeader("Authorization") String accessToken) {
        return messageService.sendMessage(messageInput,accessToken);
    }
}