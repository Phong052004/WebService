package com.example.eCommerceApp.mapper;

import com.example.eCommerceApp.dto.message.MessageInput;
import com.example.eCommerceApp.dto.message.MessageOutput;
import com.example.eCommerceApp.entity.MessageEntity;
import org.mapstruct.Mapper;

@Mapper
public interface MessageMapper {
    MessageEntity getEntityFromInput(MessageInput messageInput);
    MessageOutput getOutputFromEntity(MessageEntity messageEntity);
}