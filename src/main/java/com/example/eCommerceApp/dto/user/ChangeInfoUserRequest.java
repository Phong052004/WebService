package com.example.eCommerceApp.dto.user;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ChangeInfoUserRequest {
    private String fullName;
    private String image;
    private String gender;
    private String email;
    private String telephone;
    private LocalDateTime birthday;
}
