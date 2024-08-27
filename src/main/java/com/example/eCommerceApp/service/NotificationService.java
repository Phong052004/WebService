package com.example.eCommerceApp.service;

import com.example.eCommerceApp.base.filter.Filter;
import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.event.NotificationOutput;
import com.example.eCommerceApp.dto.user.UserOutput;
import com.example.eCommerceApp.entity.EventNotificationEntity;
import com.example.eCommerceApp.entity.NotificationEntity;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.entity.product.ProductEntity;
import com.example.eCommerceApp.mapper.NotificationMapper;
import com.example.eCommerceApp.repository.EventNotificationRepository;
import com.example.eCommerceApp.repository.NotificationRepository;
import com.example.eCommerceApp.repository.UserRepository;
import com.example.eCommerceApp.repository.product.ProductRepository;
import com.example.eCommerceApp.token.TokenHelper;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    private final EventNotificationRepository eventNotificationRepository;
    private final NotificationMapper notificationMapper;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<NotificationOutput> getNotifies(String accessToken, Pageable pageable) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);

        Page<NotificationEntity> notificationEntities = Filter.builder(NotificationService.class, entityManager)
                .filter()
                .isEqual("userId", userId)
                .orderBy("createAt", Common.DESC)
                .getPage(pageable);

        if(notificationEntities.isEmpty()) {
            return Page.empty();
        }

        Set<Long> userIds = new HashSet<>();
        Set<Long> productIds = new HashSet<>();
        List<NotificationEntity> noSeenNotifyEntities = new ArrayList<>();
        for(NotificationEntity notificationEntity : noSeenNotifyEntities) {
            if (Objects.nonNull(notificationEntity.getInteractId())) {
                userIds.add(notificationEntity.getInteractId());
            }

            if(Objects.nonNull(notificationEntity.getProductId())) {
                productIds.add(notificationEntity.getProductId());
            }

            if (Boolean.FALSE.equals(notificationEntity.getHasSeen())) {
                noSeenNotifyEntities.add(notificationEntity);
            }
        }

        Map<Long, UserEntity> interactMap = userRepository.findAllByIdIn(userIds).stream()
                .collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        Map<Long, ProductEntity> productEntityMap = productRepository.findAllByIdIn(productIds).stream()
                .collect(Collectors.toMap(ProductEntity::getId, Function.identity()));

        if (!noSeenNotifyEntities.isEmpty()) {
            CompletableFuture.runAsync(() -> {
                for (NotificationEntity notificationEntity : noSeenNotifyEntities) {
                    notificationEntity.setHasSeen(true);
                    notificationRepository.save(notificationEntity);
                }
            });
        }

        List<EventNotificationEntity> events = eventNotificationRepository.findAllByUserIdAndState(
                userId,
                Common.NEW_EVENT
        );
        if (Objects.nonNull(events) && !events.isEmpty()){
            for (EventNotificationEntity event : events){
                if (!Common.MESSAGE.equals(event.getEventType())){
                    event.setState(Common.OLD_EVENT);
                    eventNotificationRepository.save(event);
                }
            }
        }

        return notificationEntities.map(
                notificationEntity -> {
                    NotificationOutput notificationOutput = notificationMapper.getOutputFromEntity(notificationEntity);
                    if (Objects.nonNull(notificationEntity.getInteractId())) {
                        UserEntity interact = interactMap.get(notificationEntity.getInteractId());
                        ProductEntity productEntity = productEntityMap.get(notificationEntity.getProductId());
                        notificationOutput.setNameProduct(productEntity.getName());
                        notificationOutput.setInteract(
                                UserOutput.builder()
                                        .id(interact.getId())
                                        .fullName(interact.getFullName())
                                        .imageUrl(interact.getImage())
                                        .build()
                        );
                    }
                    return notificationOutput;
                }
        );
    }
}

