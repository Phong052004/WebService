package com.example.eCommerceApp.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TokenResponse {
    private String accessToken;
}
