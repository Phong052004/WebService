package com.example.eCommerceApp.dto.event;

import com.example.eCommerceApp.dto.user.UserOutput;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class NotificationOutput {
    private Long id;
    private String type; // shoppe / shop / user
    private Long userId; // của mình
    private Long interactId; // người tương tác với mình
    private String interactType; // Like, share, comment,notification
    private Long productId;
    private String nameProduct;
    private Boolean hasSeen; // false
    private LocalDateTime createdAt;
    private UserOutput interact;
}
