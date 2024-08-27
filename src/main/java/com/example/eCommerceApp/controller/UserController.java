package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.user.ChangeInfoUserRequest;
import com.example.eCommerceApp.dto.user.TokenResponse;
import com.example.eCommerceApp.dto.user.UserRequest;
import com.example.eCommerceApp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@CrossOrigin
@RequestMapping("/api/v1/user")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Đăng kí tài khoản")
    @PostMapping("/sign-up")
    public String signUp(@RequestBody UserRequest userRequest) {
        return userService.signUp(userRequest);
    }

    @Operation(summary = "Đăng nhập")
    @PostMapping("/log-in")
    public ResponseEntity logIn(@RequestBody UserRequest logInRequest) {
        return new ResponseEntity(new TokenResponse(userService.logIn(logInRequest)), HttpStatus.OK);
    }

    @Operation(summary = "Thay đổi thông tin người dùng")
    @PostMapping("/change-information")
    public void changeInformation(@RequestBody ChangeInfoUserRequest changeInfoUserRequest,
                                  @RequestHeader("Authorization") String accessToken) {
        userService.changeInformation(changeInfoUserRequest,accessToken);
    }

    @Operation(summary = "Đăng ký trở thành cửa hàng")
    @PostMapping("/register-shop")
    public void registerShop(@RequestHeader("Authorization") String accessToken) {
        userService.registerShop(accessToken);
    }
}
