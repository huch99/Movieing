package com.movieing.movieingbackend.payment.dto.admin;

import com.movieing.movieingbackend.payment.entity.PaymentMethod;
import com.movieing.movieingbackend.payment.entity.PaymentProvider;
import com.movieing.movieingbackend.payment.entity.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDetailAdminResponseDto {

    private Long paymentId;
    private String publicPaymentId;
    private Long bookingId;
    private String bookingNo;
    private Long userId;
    private String userName;
    private String userPhone;
    private String userEmail;
    private PaymentStatus status;
    private PaymentProvider provider;
    private PaymentMethod method;
    private String pgTxId;
    private String approvedAt;
    private Double amount;

}
