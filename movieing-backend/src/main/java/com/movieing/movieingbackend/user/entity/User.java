package com.movieing.movieingbackend.user.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name="idx_user_email_active", columnList="email, is_active"),
                @Index(name = "idx_user_role", columnList = "role")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "public_user_id", nullable = false, length = 36, unique = true)
    private String publicUserId;        // uuid 형식

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "phone", nullable = false, length = 20, unique = true)
    private String phone;

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private boolean isActive = true;

    @Column(name = "agree_at", nullable = false)
    private LocalDateTime agreeAt;

    /* ======== Method ======== */
    @PrePersist
    void onCreate() {
        if (agreeAt == null) agreeAt = LocalDateTime.now();
    }

    public void changePassword(String newHash) {
        this.passwordHash = newHash;
    }

    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }
}
