package com.example.eCommerceApp.token;

import com.example.eCommerceApp.service.EventNotificationService;

public class EventHelper {
    public static void pushEventForUserByUserId(Long userId){
        if (!EventNotificationService.map1.containsKey(userId)) {
            EventNotificationService.map1.put(userId, 0);
            EventNotificationService.map2.put(userId, 0);
        }
        EventNotificationService.map1.put(
                userId,
                EventNotificationService.map1.get(userId) + 1
        );
    }
}

