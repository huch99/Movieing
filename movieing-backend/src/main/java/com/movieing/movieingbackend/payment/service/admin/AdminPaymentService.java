package com.movieing.movieingbackend.payment.service.admin;

import com.movieing.movieingbackend.payment.dto.admin.PaymentDetailAdminResponseDto;
import com.movieing.movieingbackend.payment.dto.admin.PaymentListItemAdminResponseDto;
import com.movieing.movieingbackend.payment.entity.Payment;
import com.movieing.movieingbackend.payment.entity.PaymentStatus;
import com.movieing.movieingbackend.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminPaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional(readOnly = true)
    public Page<PaymentListItemAdminResponseDto> getList(Pageable pageable) {
        return paymentRepository.findAll(pageable)
                .map(payment -> PaymentListItemAdminResponseDto.builder()
                        .paymentId(payment.getPaymentId())
                        .publicPaymentId(payment.getPublicPaymentId())
                        .bookingId(payment.getBooking().getBookingId())
                        .bookingNo(payment.getBooking().getBookingNo())
                        .userId(payment.getUser().getUserId())
                        .userName(payment.getUser().getUserName())
                        .approvedAt(payment.getApprovedAt())
                        .amount(payment.getAmount())
                        .status(payment.getStatus())
                        .build());
    }

    @Transactional(readOnly = true)
    public PaymentDetailAdminResponseDto getDetail(Long paymentId) {
        Payment payment = getPayment(paymentId);

        return PaymentDetailAdminResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .publicPaymentId(payment.getPublicPaymentId())
                .bookingId(payment.getBooking().getBookingId())
                .bookingNo(payment.getBooking().getBookingNo())
                .userId(payment.getUser().getUserId())
                .userName(payment.getUser().getUserName())
                .userPhone(payment.getUser().getPhone())
                .userEmail(payment.getUser().getEmail())
                .status(payment.getStatus())
                .provider(payment.getProvider())
                .method(payment.getMethod())
                .pgTxId(payment.getPgTxId())
                .approvedAt(payment.getApprovedAt().toString())
                .amount(payment.getAmount())
                .build();
    }

    @Transactional
    public void refunded(Long paymentId) {
        paymentRepository.findById(paymentId)
                .ifPresent(payment -> {
                    if (payment.getStatus() == PaymentStatus.PAID) {
                        payment.refunded();
                    }
                });
    }

    private Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 내역을 찾을 수 없습니다."));
    }
}
