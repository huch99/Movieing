package com.movieing.movieingbackend.booking.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.booking_seat.entity.BookingSeatStatus;
import com.movieing.movieingbackend.schedule.entity.Schedule;
import com.movieing.movieingbackend.screen.entity.Screen;
import com.movieing.movieingbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@Table(name = "booking")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id", nullable = false)
    private Long bookingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    @Column(name = "booking_no", nullable = false, length = 50)
    private String bookingNo;

    @Column(name = "status", nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount;

    /* ======== Lifecycle ======== */

    @PrePersist
    void onCreate() {
        if (bookingNo == null) {
            bookingNo = "MVI-" + System.currentTimeMillis();
        }
    }

    /* ======== Domain Methods ======== */

    public void changeStatus(BookingStatus status) {
        this.status = status;
    }

    public void cancel() {
        this.status = BookingStatus.CANCELED;
    }
}
