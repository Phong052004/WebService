package com.example.eCommerceApp.repository;

import com.example.eCommerceApp.entity.EventNotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventNotificationRepository extends JpaRepository<EventNotificationEntity, Long> {
    void deleteAllByUserIdAndChatId(Long userId, Long chatId);

    List<EventNotificationEntity> findAllByUserIdAndEventType(Long userId, String eventType);

    List<EventNotificationEntity> findAllByUserId(Long userId);

    List<EventNotificationEntity> findAllByUserIdAndState(Long userId, String state);
}

