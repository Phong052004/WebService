package com.example.eCommerceApp.service;

import com.example.eCommerceApp.base.filter.Filter;
import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.chat.ChatOutput;
import com.example.eCommerceApp.dto.message.MessageOutput;
import com.example.eCommerceApp.entity.ChatEntity;
import com.example.eCommerceApp.entity.EventNotificationEntity;
import com.example.eCommerceApp.entity.MessageEntity;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.mapper.ChatMapper;
import com.example.eCommerceApp.mapper.MessageMapper;
import com.example.eCommerceApp.repository.ChatRepository;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.EventNotificationRepository;
import com.example.eCommerceApp.repository.UserRepository;
import com.example.eCommerceApp.token.TokenHelper;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class ChatService {
    private final ChatRepository chatRepository;
    private final EntityManager entityManager;
    private final ChatMapper chatMapper;
    private final EventNotificationRepository eventNotificationRepository;
    private final CustomRepository customRepository;
    private final UserRepository userRepository;
    private final MessageMapper messageMapper;

    @Transactional
    public Page<MessageOutput> getMessages(String accessToken, Long chatId, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        eventNotificationRepository.deleteAllByUserIdAndChatId(userId, chatId);

        Page<MessageEntity> messageEntities = Filter.builder(MessageEntity.class, entityManager)
                .search()
                .isEqual("chatId1", chatId)
                .isEqual("chatId2", chatId)
                .orderBy("createAt", Common.DESC)
                .getPage(pageable);

        List<Long> userIds = new ArrayList<>();
        ChatEntity chatEntity = customRepository.getChatEntityBy(chatId);
        userIds.add(chatEntity.getUserId1());
        userIds.add(chatEntity.getUserId2());

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(userIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return messageEntities.map(
                messageEntity -> {
                    MessageOutput messageOutput = messageMapper.getOutputFromEntity(messageEntity);
                    if(userEntityMap.containsKey(messageEntity.getSenderId())) {
                        UserEntity userEntity = userEntityMap.get(messageEntity.getSenderId());
                        messageOutput.setUserId(userEntity.getId());
                        messageOutput.setFullName(userEntity.getFullName());
                        messageOutput.setImageUrl(userEntity.getImage());
                        messageOutput.setIsMe(userId.equals(userEntity.getId()));
                    }
                    return messageOutput;
                }
        );
    }

    @Transactional
    public Page<ChatOutput> getChatList(String search, String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<Long> chatIds = null;
        List<ChatEntity> chatEntities = chatRepository.findAllByUserId2(userId);

        if(Objects.nonNull(chatEntities) && !chatEntities.isEmpty()) {
            chatIds = chatEntities.stream().map(ChatEntity::getId).collect(Collectors.toList());
        }

        Page<ChatEntity> chatEntityPage = Filter.builder(ChatEntity.class, entityManager)
                .search()
                .isIn("id", chatIds)
                .isEqual("userId2", userId)
                .filter()
                .isContain("name", search)
                .isNotNull("newestChatTime")
                .orderBy("newestChatTime", Common.DESC)
                .getPage(pageable);

        Map<Long, List<EventNotificationEntity>> eventNotificationMap =
                eventNotificationRepository.findAllByUserIdAndEventType(userId, Common.MESSAGE).stream()
                        .collect(Collectors.groupingBy(EventNotificationEntity::getChatId));


        return chatEntityPage.map(chatEntity -> {
            ChatOutput chatOutput = chatMapper.getOutputFromEntity(chatEntity);
            if (eventNotificationMap.containsKey(chatOutput.getId())) {
                chatOutput.setMessageCount(eventNotificationMap.get(chatOutput.getId()).size());
            } else {
                chatOutput.setMessageCount(0);
            }
            chatOutput.setIsMe(userId.equals(chatEntity.getNewestUserId()));
            return chatOutput;
        });
    }
}

