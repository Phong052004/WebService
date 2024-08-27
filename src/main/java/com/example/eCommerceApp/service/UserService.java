package com.example.eCommerceApp.service;

import com.example.eCommerceApp.common.Common;
import com.example.eCommerceApp.dto.user.ChangeInfoUserRequest;
import com.example.eCommerceApp.dto.user.UserRequest;
import com.example.eCommerceApp.entity.UserEntity;
import com.example.eCommerceApp.mapper.UserMapper;
import com.example.eCommerceApp.repository.CustomRepository;
import com.example.eCommerceApp.repository.UserRepository;
import com.example.eCommerceApp.token.TokenHelper;
import lombok.AllArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CustomRepository customRepository;

    @Transactional
    public String signUp(UserRequest signUpRequest) {
        if(Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new RuntimeException(Common.USERNAME_IS_EXISTS);
        }
        signUpRequest.setPassword(BCrypt.hashpw(signUpRequest.getPassword(), BCrypt.gensalt()));
        UserEntity userEntity = userMapper.getEntityFromInput(signUpRequest);
        UUID uuid = UUID.randomUUID();
        userEntity.setFullName("USER" + uuid);
        userEntity.setIsShop(Boolean.FALSE);
        userEntity.setAverageRating(0.0);
        userEntity.setFollowing(0);
        userEntity.setFollowers(0);
        userEntity.setTotalComment(0);
        userRepository.save(userEntity);
        return TokenHelper.generateToken(userEntity);
    }

    @Transactional
    public String logIn(UserRequest logInRequest) {
        UserEntity userEntity = userRepository.findByUsername(logInRequest.getUsername());
        if(Objects.isNull(userEntity)) {
            throw new RuntimeException(Common.ACTION_FAIL);
        }
        String currentHashedPassword = userEntity.getPassword();
        if(BCrypt.checkpw(logInRequest.getPassword(),currentHashedPassword)) {
            return TokenHelper.generateToken(userEntity);
        }
        throw new RuntimeException(Common.INCORRECT_PASSWORD);
    }

    @Transactional
    public void changeInformation(ChangeInfoUserRequest changeInfoUserRequest, String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        userMapper.updateEntityFromInput(userEntity,changeInfoUserRequest);
        userRepository.save(userEntity);
    }

    @Transactional
    public void registerShop(String accessToken) {
        Long userId = TokenHelper.getUserIdFromToken(accessToken);
        UserEntity userEntity = customRepository.getUserBy(userId);
        userEntity.setTotalProduct(0);
        userEntity.setTotalComment(0);
        userEntity.setIsShop(Boolean.TRUE);
        userRepository.save(userEntity);
    }
}
