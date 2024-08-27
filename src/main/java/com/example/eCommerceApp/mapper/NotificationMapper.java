package com.example.eCommerceApp.mapper;

import com.example.eCommerceApp.dto.event.MessageEventOutput;
import com.example.eCommerceApp.dto.event.NotificationOutput;
import com.example.eCommerceApp.entity.EventNotificationEntity;
import com.example.eCommerceApp.entity.NotificationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface NotificationMapper {
    NotificationOutput getOutputFromEntity(NotificationEntity notificationEntity);
    MessageEventOutput getOutputFromEntity(EventNotificationEntity eventNotificationEntity);
}
