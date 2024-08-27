package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.event.EventCountOutput;
import com.example.eCommerceApp.dto.event.MessageEventOutput;
import com.example.eCommerceApp.entity.EventNotificationEntity;
import com.example.eCommerceApp.mapper.NotificationMapper;
import com.example.eCommerceApp.repository.EventNotificationRepository;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class EventNotificationService {
    private final EventNotificationRepository eventNotificationRepository;
    public static volatile Map<Long, Integer> map1 = new HashMap<>(); //currentNewMessage
    public static volatile Map<Long, Integer> map2 = new HashMap<>(); //oldNewMessage
    private final NotificationMapper notificationMapper;

    @Transactional
    public EventCountOutput getEvent(String accessToken, Long chatId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if (!map1.containsKey(userId)){
            System.out.println("FIRST CONNECT OF USER " + userId);
            List<EventNotificationEntity> eventNotificationEntities = eventNotificationRepository.findAllByUserId(userId);
            map1.put(userId, eventNotificationEntities.size());
            map2.put(userId,
                    eventNotificationEntities.stream()
                            .filter(e -> e.getState().equals(Common.OLD_EVENT))
                            .collect(Collectors.toList()).size()
            );
        }

        System.out.println("MAP HAVE:");
        System.out.println(map1);
        System.out.println(map2);

        while (true) {
            if (!map1.get(userId).equals(map2.get(userId))) {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    throw new RuntimeException(Common.ACTION_FAIL);
                }
                System.out.println("NEW EVENT FOR USER_ID :" + userId);
                System.out.println("MAP HAVE:");
                System.out.println(map1);
                System.out.println(map2);
                map2.put(userId, map1.get(userId));
                System.out.println("MAP 2 AFTER PUSH: " + map2.get(userId));
                List<EventNotificationEntity> events = eventNotificationRepository.findAllByUserId(userId);

                List<EventNotificationEntity> newEvents = new ArrayList<>();
                for (EventNotificationEntity event : events) {
                    if (Common.NEW_EVENT.equals(event.getState())) {
                        newEvents.add(event);
                    }
                }
                Set<Long> messagesForCount = new HashSet<>();
                List<MessageEventOutput> messageEventOutputs = new ArrayList<>();
                if (!newEvents.isEmpty()) {
                    EventCountOutput eventCountOutput = new EventCountOutput();
                    for (EventNotificationEntity event : events) {
                        if (Common.MESSAGE.equals(event.getEventType()) && Common.NEW_EVENT.equals(event.getState())) {
                            messagesForCount.add(event.getChatId());
                            MessageEventOutput messageEventOutput = notificationMapper.getOutputFromEntity(event);
                            messageEventOutput.setIsMe(false);
                            messageEventOutputs.add(messageEventOutput);
                        }
                        else if (!Common.MESSAGE.equals(event.getEventType()) && Common.NEW_EVENT.equals(event.getState())){
                            eventCountOutput.setInformCount(eventCountOutput.getInformCount() + 1);
                        }
                    }
                    for (EventNotificationEntity newEvent : newEvents) {
//                        if (!Common.MESSAGE.equals(newEvent.getEventType())){
//                            newEvent.setState(Common.OLD_EVENT);
//                        }
                        // them
                        if (Common.MESSAGE.equals(newEvent.getEventType())
                                && Objects.nonNull(chatId) && newEvent.getChatId().equals(chatId)){
                            newEvent.setState(Common.OLD_EVENT);
                        }
                    }
                    eventCountOutput.setMessageCount(messagesForCount.size());
                    if (!messageEventOutputs.isEmpty()){
                        eventCountOutput.setMessages(
                                messageEventOutputs.stream()
                                        .sorted(Comparator.comparing(MessageEventOutput::getCreatedAt))
                                        .collect(Collectors.toList())
                        );
                    }
                    eventNotificationRepository.saveAll(newEvents);
                    return eventCountOutput;
                }
            }
        }
    }

    @Transactional
    public void deleteMessageEvent(String accessToken, Long chatId){
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        List<EventNotificationEntity> events = eventNotificationRepository.findAllByUserId(userId);
        List<EventNotificationEntity> newMessageEvents = new ArrayList<>();

        if (Objects.nonNull(events) && !events.isEmpty()){
            for (EventNotificationEntity event : events) {
                if (Common.NEW_EVENT.equals(event.getState())
                        && Common.MESSAGE.equals(event.getEventType())
                        && Objects.nonNull(chatId) && event.getChatId().equals(chatId)){
                    event.setState(Common.OLD_EVENT);
                    newMessageEvents.add(event);
                }
            }
            if (!events.isEmpty()){
                eventNotificationRepository.saveAll(newMessageEvents);
            }
        }
    }
}

