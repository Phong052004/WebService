package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.comment.CommentInput;
import com.example.eCommerceApp.dto.comment.CommentOutput;
import com.example.eCommerceApp.entity.*;
import com.example.eCommerceApp.entity.product.ProductTemplateEntity;
import com.example.eCommerceApp.helper.StringUtils;
import com.example.eCommerceApp.repository.*;
import com.example.eCommerceApp.repository.product.ProductTemplateRepository;
import com.example.eCommerceApp.token.EventHelper;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserInteractService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final CustomRepository customRepository;
    private final LikeRepository likeRepository;
    private final FollowingShopRepository followingShopRepository;
    private final NotificationRepository notificationRepository;
    private final EventNotificationRepository eventNotificationRepository;
    private final ChatRepository chatRepository;
    private final ProductTemplateRepository productTemplateRepository;

    @Transactional
    public void comment(String accessToken, Long productTemplateId, CommentInput commentInput) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        UserEntity shopEntity = customRepository.getUserBy(productTemplateEntity.getShopId());
        UserEntity userEntity = customRepository.getUserBy(userId);

        CompletableFuture.runAsync(() -> {
            commentRepository.save(
                    CommentEntity.builder()
                            .userId(userId)
                            .productTemplateId(productTemplateId)
                            .comment(commentInput.getComment())
                            .images(StringUtils.getStringFromList(commentInput.getImageUrls()))
                            .createAt(LocalDateTime.now())
                            .build()
            );

            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .type(Common.USER)
                    .userId(productTemplateEntity.getShopId())
                    .interactId(userId)
                    .interactType(Common.COMMENT)
                    .productId(productTemplateId)
                    .hasSeen(Boolean.FALSE)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notificationEntity);

            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .userId(productTemplateEntity.getShopId())
                            .state(Common.NEW_EVENT)
                            .eventType(Common.COMMENT)
                            .fullName(userEntity.getFullName())
                            .imageUrl(userEntity.getImage())
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            EventHelper.pushEventForUserByUserId(shopEntity.getId());
        });

        double averageRatingProduct = productTemplateEntity.getAverageRating() * productTemplateEntity.getCommentCount();
        double averageRatingShop = shopEntity.getAverageRating() * shopEntity.getTotalProduct();

        averageRatingShop = averageRatingShop - productTemplateEntity.getAverageRating();
        averageRatingProduct += commentInput.getRating();

        int commentCountProduct = productTemplateEntity.getCommentCount();
        commentCountProduct++;
        productTemplateEntity.setCommentCount(commentCountProduct);
        productTemplateEntity.setAverageRating(averageRatingProduct/commentCountProduct);
        productTemplateRepository.save(productTemplateEntity);

        int commentCountShop = shopEntity.getTotalComment();
        commentCountShop++;
        shopEntity.setTotalComment(commentCountShop);
        shopEntity.setAverageRating(
                (averageRatingShop + (averageRatingProduct/commentCountProduct))/shopEntity.getTotalProduct()
        );
        userRepository.save(shopEntity);
    }

    @Transactional
    public void removeComment(String accessToken, Long commentId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        CommentEntity commentEntity = commentRepository.findByIdAndUserId(commentId,userId);

        Long productTemplateId = commentEntity.getProductTemplateId();
        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        UserEntity shopEntity = customRepository.getUserBy(productTemplateEntity.getShopId());

        double averageRatingProduct = productTemplateEntity.getAverageRating() * productTemplateEntity.getCommentCount();
        double averageRatingShop = shopEntity.getAverageRating() * shopEntity.getTotalProduct();

        averageRatingShop = averageRatingShop - productTemplateEntity.getAverageRating();
        averageRatingProduct -= commentEntity.getRating();

        int commentCountProduct = productTemplateEntity.getCommentCount();
        commentCountProduct--;
        productTemplateEntity.setCommentCount(commentCountProduct);
        productTemplateEntity.setAverageRating(averageRatingProduct/commentCountProduct);
        productTemplateRepository.save(productTemplateEntity);

        int commentCountShop = shopEntity.getTotalComment();
        commentCountShop--;
        shopEntity.setTotalComment(commentCountShop);
        shopEntity.setAverageRating(
                (averageRatingShop + (averageRatingProduct/commentCountProduct))/shopEntity.getTotalProduct()
        );
        userRepository.save(shopEntity);

        commentRepository.deleteByIdAndUserId(commentId,userId);
    }

    @Transactional
    public void likeProduct(String accessToken, Long productTemplateId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        if(Boolean.TRUE.equals(likeRepository.existsByProductTemplateIdAndUserId(productTemplateId, userId))) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }

        ProductTemplateEntity productTemplateEntity = customRepository.getProductTemplateBy(productTemplateId);
        UserEntity userEntity = customRepository.getUserBy(userId);

        productTemplateEntity.setLikeCount(productTemplateEntity.getLikeCount() + 1);
        productTemplateRepository.save(productTemplateEntity);

        CompletableFuture.runAsync(() -> {
            likeRepository.save(
                    LikeEntity.builder()
                            .userId(userId)
                            .productTemplateId(productTemplateId)
                            .build()
            );

            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .type(Common.USER)
                    .userId(productTemplateEntity.getShopId())
                    .interactId(userId)
                    .interactType(Common.LIKE)
                    .productId(productTemplateId)
                    .hasSeen(Boolean.FALSE)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notificationEntity);

            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .userId(productTemplateEntity.getShopId())
                            .state(Common.NEW_EVENT)
                            .eventType(Common.LIKE)
                            .fullName(userEntity.getFullName())
                            .imageUrl(userEntity.getImage())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            EventHelper.pushEventForUserByUserId(productTemplateEntity.getShopId());
        });
    }

    @Transactional(readOnly = true)
    public Page<CommentOutput> getCommentsOfProduct(Long productTemplateId, Pageable pageable) {
        Page<CommentEntity> commentEntities = commentRepository.findAllByProductTemplateId(productTemplateId, pageable);
        if(Objects.isNull(commentEntities) || commentEntities.isEmpty()) {
            return Page.empty();
        }

        List<Long> userIds = new ArrayList<>();
        for(CommentEntity commentEntity : commentEntities) {
            userIds.add(commentEntity.getUserId());
        }

        Map<Long, UserEntity> userEntityMap = userRepository.findAllByIdIn(userIds)
                .stream().collect(Collectors.toMap(UserEntity::getId, Function.identity()));

        return commentEntities.map(
                commentEntity -> {
                    UserEntity userEntity = userEntityMap.get(commentEntity.getUserId());
                    return CommentOutput.builder()
                            .userId(userEntity.getId())
                            .fullName(userEntity.getFullName())
                            .image(userEntity.getImage())
                            .creatAt(commentEntity.getCreateAt())
                            .rating(commentEntity.getRating())
                            .comment(commentEntity.getComment())
                            .imageUrlsProduct(StringUtils.getListFromString(commentEntity.getImages()))
                            .build();
                }
        );
    }

    @Transactional
    public void followShop(String accessToken, Long shopId) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity shopEntity = customRepository.getUserBy(shopId);
        UserEntity userEntity = customRepository.getUserBy(userId);

        FollowingShopEntity followingShopEntity = FollowingShopEntity.builder()
                .userId(userId)
                .shopId(shopId)
                .build();
        followingShopRepository.save(followingShopEntity);

        CompletableFuture.runAsync(() -> {
            shopEntity.setFollowers(shopEntity.getFollowers() + 1);
            userRepository.save(shopEntity);

            NotificationEntity notificationEntity = NotificationEntity.builder()
                    .type(Common.USER)
                    .userId(shopId)
                    .interactId(userId)
                    .interactType(Common.LIKE)
                    .hasSeen(Boolean.FALSE)
                    .createdAt(LocalDateTime.now())
                    .build();

            notificationRepository.save(notificationEntity);

            eventNotificationRepository.save(
                    EventNotificationEntity.builder()
                            .userId(shopId)
                            .state(Common.NEW_EVENT)
                            .eventType(Common.LIKE)
                            .fullName(userEntity.getFullName())
                            .imageUrl(userEntity.getImage())
                            .createdAt(LocalDateTime.now())
                            .build()
            );

            chatRepository.save(
                    ChatEntity.builder()
                            .name(userEntity.getFullName())
                            .imageUrl(userEntity.getImage())
                            .userId1(userId)
                            .userId2(shopId)
                            .newestUserId(1L)
                            .newestChatTime(LocalDateTime.now())
                            .newestMessage("Bạn vừa follow")
                            .build()
            );

            chatRepository.save(
                    ChatEntity.builder()
                            .name(shopEntity.getFullName())
                            .imageUrl(shopEntity.getImage())
                            .userId1(shopId)
                            .userId2(userId)
                            .newestUserId(1L)
                            .newestChatTime(LocalDateTime.now())
                            .newestMessage("Bạn vừa được follow")
                            .build()
            );

            EventHelper.pushEventForUserByUserId(shopId);
        });
    }
}
