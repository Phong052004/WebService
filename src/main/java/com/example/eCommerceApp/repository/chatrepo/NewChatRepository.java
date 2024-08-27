package com.example.eCommerceApp.repository.chatrepo;

import com.example.eCommerceApp.base.adapter.BaseRepository;
import com.example.eCommerceApp.entity.ChatEntity;

public interface NewChatRepository extends BaseRepository<ChatEntity> {
    ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2);
}
