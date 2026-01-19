package com.movieing.movieingbackend.payment.entity;

import com.movieing.movieingbackend.aspect.BaseTimeEntity;
import com.movieing.movieingbackend.booking.entity.Booking;
import com.movieing.movieingbackend.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(name = "payment")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id", nullable = false)
    private Long paymentId;

    @Column(name = "public_payment_id", nullable = false, length = 36)
    private String publicPaymentId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false, unique = true)
    private Booking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentProvider provider = PaymentProvider.CARD;

    @Column(name = "method", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentMethod method = PaymentMethod.CARD;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.READY;

    @Column(name = "pg_tx_id", nullable = true, length = 100)
    private String pgTxId;

    @Column(name = "approved_at", nullable = true)
    private LocalDateTime approvedAt;

    @PrePersist
    void onCreate() {
        this.publicPaymentId = "PAYMENT" + UUID.randomUUID().toString();
    }

    public void refunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public void paid() {
        this.status = PaymentStatus.PAID;
    }

    public void failed() {
        this.status = PaymentStatus.FAILED;
    }

    public void confirm() {
        paid();
        approvedAt = LocalDateTime.now();
    }
}
