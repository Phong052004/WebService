package com.example.eCommerceApp.controller;

import com.example.eCommerceApp.dto.event.NotificationOutput;
import com.example.eCommerceApp.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@CrossOrigin
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @Operation(summary = "Lấy danh sách thông báo")
    @GetMapping
    public Page<NotificationOutput> getNotifies(@RequestHeader("Authorization") String accessToken,
                                                @ParameterObject Pageable pageable){
        return notificationService.getNotifies(accessToken, pageable);
    }
}
