package com.example.eCommerceApp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "tbl_user")
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String image;
    private LocalDateTime birthday;
    private String gender;
    private String email;
    private Long phoneNumber;
    private Boolean isShop;
    private Integer following;
    private Integer followers;
    private Double averageRating;
    private Integer totalProduct;
    private Integer totalComment;
}
