package com.example.eCommerceApp.mapper;

import com.example.eCommerceApp.dto.user.ChangeInfoUserRequest;
import com.example.eCommerceApp.dto.user.UserRequest;
import com.example.eCommerceApp.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserMapper {
    UserEntity getEntityFromInput(UserRequest userRequest);

    void updateEntityFromInput(@MappingTarget UserEntity userEntity, ChangeInfoUserRequest changeInfoUserRequest);
}
