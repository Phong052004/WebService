package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.message.MessageInput;
import com.example.eCommerceApp.entity.ChatEntity;
import com.example.eCommerceApp.entity.EventNotificationEntity;
import com.example.eCommerceApp.entity.MessageEntity;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.mapper.MessageMapper;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.EventNotificationRepository;
import com.example.eCommerceApp.repository.MessageRepository;
import com.example.eCommerceApp.repository.chatrepo.NewChatRepository;
import com.example.eCommerceApp.token.EventHelper;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Service
public class MessageService {
    private final MessageRepository messageRepository;
    private final MessageMapper messageMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final CustomRepository customRepository;
    private final NewChatRepository newChatRepository;

    @Transactional
    public String sendMessage(MessageInput messageInput, String accessToken) {
        LocalDateTime now = LocalDateTime.now();
        Long senderId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity sender = customRepository.getUserBy(senderId);
        ChatEntity chatEntity = customRepository.getChatEntityBy(messageInput.getChatId());
        chatEntity.setNewestUserId(senderId);
        chatEntity.setNewestMessage(messageInput.getMessage());
        chatEntity.setNewestChatTime(now);

        MessageEntity messageEntity = messageMapper.getEntityFromInput(messageInput);
        messageEntity.setSenderId(senderId);
        messageEntity.setCreatedAt(LocalDateTime.now());

        ChatEntity chatEntity2 = newChatRepository.findByUserId1AndUserId2(chatEntity.getUserId2(), chatEntity.getUserId1());
        Long chatId2 = chatEntity2.getId();
        messageEntity.setChatId1(chatEntity.getId());
        messageEntity.setChatId2(chatEntity2.getId());
        chatEntity2.setNewestMessage(messageInput.getMessage());
        chatEntity2.setNewestUserId(senderId);
        chatEntity2.setNewestChatTime(now);
        newChatRepository.save(chatEntity2);
        messageRepository.save(messageEntity);

        CompletableFuture.runAsync(() -> {
            newChatRepository.save(chatEntity);

            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .eventType(Common.MESSAGE)
                            .userId(chatEntity.getUserId2())
                            .imageUrl(sender.getImage())
                            .fullName(sender.getFullName())
                            .state(Common.NEW_EVENT)
                            .chatId(chatId2)
                            .createdAt(now)
                            .message(messageInput.getMessage())
                            .build()
            );
            EventHelper.pushEventForUserByUserId(chatEntity.getUserId2());
        });
        return messageInput.getMessage();
    }

}
