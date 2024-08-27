package com.example.eCommerceApp.mapper;

import com.example.eCommerceApp.dto.chat.ChatOutput;
import com.example.eCommerceApp.entity.ChatEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ChatMapper {
    ChatOutput getOutputFromEntity(ChatEntity chatEntity);
}
