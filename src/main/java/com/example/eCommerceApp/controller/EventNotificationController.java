package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.event.EventCountOutput;
import com.example.eCommerceApp.service.EventNotificationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/event-notification")
public class EventNotificationController {
    private final EventNotificationService eventNotificationService;

    @DeleteMapping
    public void deleteMessageEvent(@RequestHeader(value = "Authorization") String accessToken,
                                   @RequestParam Long chatId){
        eventNotificationService.deleteMessageEvent(accessToken, chatId);
    }

    @GetMapping
    public EventCountOutput getEvents(@RequestHeader(value = "Authorization") String accessToken,
                                      @RequestParam(required = false) Long chatId){
        return eventNotificationService.getEvent(accessToken, chatId);
    }
}