package com.movieing.movieingbackend.user.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자(User) 엔티티
 * <p>
 * - 서비스 전반에서 사용하는 사용자 기본 정보 엔티티
 * - 인증/인가(JWT), 예매, 결제, 즐겨찾기 등 대부분의 도메인에서 참조
 * - 물리 삭제 대신 isActive 플래그로 활성/비활성 상태를 관리
 * <p>
 * 특징:
 * - 내부 식별자(userId)와 외부 노출용 식별자(publicUserId)를 분리
 * - 비밀번호는 해시 값만 저장
 * - JPA Auditing(BaseTimeEntity)을 통해 생성/수정 시간 자동 관리
 */
@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name = "idx_user_email_active", columnList = "email, is_active"),
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
    private Long userId;                // 내부 PK

    @Column(name = "public_user_id", nullable = false, length = 36, unique = true)
    private String publicUserId;        // 외부 노출용 사용자 ID (UUID)

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;            // 사용자 이름

    @Column(name = "email", nullable = false, length = 255, unique = true)
    private String email;               // 로그인용 이메일

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;        // 비밀번호 해시값 (원문 저장 금지)

    @Column(name = "phone", nullable = false, length = 20, unique = true)
    private String phone;               // 휴대폰 번호

    @Column(name = "role", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.USER;   // 사용자 권한 (USER / ADMIN / THEATER 등)

    @Column(name = "is_active", nullable = false, columnDefinition = "TINYINT(1)")
    @Builder.Default
    private boolean isActive = true;     // 활성 여부 (탈퇴/차단 시 false)

    @Column(name = "agree_at", nullable = false)
    private LocalDateTime agreeAt;       // 약관 동의 일시

    /* ======== Lifecycle ======== */

    /**
     * 엔티티 최초 저장 시 약관 동의 시간이 없으면 현재 시각으로 자동 설정
     */
    @PrePersist
    void onCreate() {
        if (publicUserId == null) {
            publicUserId = UUID.randomUUID().toString();
        }

        if (agreeAt == null) {
            agreeAt = LocalDateTime.now();
        }
    }

    /* ======== Domain Methods ======== */

    /**
     * 비밀번호 변경
     * - 반드시 암호화(해시)된 값만 전달해야 함
     */
    public void changePassword(String newHash) {
        this.passwordHash = newHash;
    }

    /**
     * 이메일 변경
     */
    public void changeEmail(String newEmail) {
        this.email = newEmail;
    }

    /**
     * 사용자 활성화
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * 사용자 비활성화 (탈퇴/차단)
     */
    public void deactivate() {
        this.isActive = false;
    }
}
