package com.example.eCommerceApp.repository.chatrepo;

import com.example.eCommerceApp.base.adapter.BaseRepositoryAdapter;
import com.example.eCommerceApp.entity.ChatEntity;
import com.example.eCommerceApp.repository.ChatRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@AllArgsConstructor
@Repository
public class NewChatRepositoryImpl extends BaseRepositoryAdapter<ChatEntity> implements NewChatRepository {
    private final ChatRepository chatRepository;

    @Override
    public ChatEntity findByUserId1AndUserId2(Long userId1, Long userId2) {
        return chatRepository.findByUserId1AndUserId2(userId1, userId2);
    }
}
