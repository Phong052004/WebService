package com.example.eCommerceApp.dto.event;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EventCountOutput {
    private int messageCount;
    private int informCount;
    private List<MessageEventOutput> messages;
}

