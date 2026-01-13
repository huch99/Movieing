package com.movieing.movieingbackend.user.entity;

import com.movieing.movieingbackend.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자(UserAdmin) 엔티티
 *
 * - 일반 사용자(User) 중 관리자 권한을 가진 사용자에 대한 추가 정보 엔티티
 * - User 엔티티와 1:1 관계를 가지며, PK를 공유하는 구조
 * - 특정 영화관(Theater)에 소속된 관리자 정보를 표현하기 위해 사용
 *
 * 설계 의도:
 * - User : 공통 사용자 정보 (로그인, 권한, 인증)
 * - UserAdmin : 관리자 전용 정보 (관리 대상 영화관 등)
 *
 * 특징:
 * - @MapsId를 사용하여 User의 PK(user_id)를 그대로 사용
 * - 관리자 계정이 특정 영화관에 귀속될 수 있도록 theater 연관 관계를 가짐
 */
@Entity
@Table(name = "users_admin")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAdmin {

    @Id
    @Column(name = "user_id")
    private Long userId;            // User와 공유하는 PK

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;              // 관리자 사용자(User)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = true)
    private Theater theater;        // 관리 대상 영화관 (없을 수도 있음)

}
