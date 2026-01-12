package com.movieing.movieingbackend.user.entity;

import com.movieing.movieingbackend.theater.entity.Theater;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users_admin")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserAdmin {

    @Id
    @Column(name="user_id")
    private Long userId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theater_id", nullable = true)
    private Theater theater;

}
