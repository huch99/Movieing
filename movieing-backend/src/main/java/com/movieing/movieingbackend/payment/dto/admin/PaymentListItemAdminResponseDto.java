package com.movieing.movieingbackend.payment.dto.admin;

import com.movieing.movieingbackend.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentListItemAdminResponseDto {

    private Long paymentId;
    private String publicPaymentId;
    private Long bookingId;
    private String bookingNo;
    private Long userId;
    private String userName;
    private LocalDateTime approvedAt;
    private Double amount;
    private PaymentStatus status;
}
