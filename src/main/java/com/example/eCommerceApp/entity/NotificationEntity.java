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
@Table(name = "tbl_notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // shoppe / shop / user
    private Long userId; // của mình
    private Long interactId; // người tương tác với mình
    private String interactType; // Like, share, comment,notification
    private Long productId;
    private Boolean hasSeen; // false
    private LocalDateTime createdAt;
}
