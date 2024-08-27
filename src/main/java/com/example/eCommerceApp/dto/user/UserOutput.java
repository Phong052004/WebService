package com.example.eCommerceApp.dto.user;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserOutput {
    private Long id;
    private String fullName;
    private String imageUrl;
}
