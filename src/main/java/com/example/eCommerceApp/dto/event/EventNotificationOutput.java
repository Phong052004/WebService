package com.example.eCommerceApp.dto.event;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventNotificationOutput {
    private Long id;
    private Long userId; // phuc = 2
    private String eventType; // message
    private String content;
}

