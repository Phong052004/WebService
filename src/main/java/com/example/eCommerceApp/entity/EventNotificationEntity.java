package com.example.eCommerceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Builder
@Table(name = "tbl_event_notification")
public class EventNotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private String fullName;
    private String imageUrl;
    private String eventType;
    private String state;
    private Long chatId;
    private String message;
    private LocalDateTime createdAt;
}
