package com.movieing.movieingbackend.payment.controller.admin;

import com.movieing.movieingbackend.payment.dto.admin.PaymentDetailAdminResponseDto;
import com.movieing.movieingbackend.payment.dto.admin.PaymentListItemAdminResponseDto;
import com.movieing.movieingbackend.payment.service.admin.AdminPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/payments")
public class AdminPaymentController {

    private final AdminPaymentService adminPaymentService;

    @GetMapping("/getList")
    public Page<PaymentListItemAdminResponseDto> getList(Pageable pageable) {
        return adminPaymentService.getList(pageable);
    }

    @GetMapping("/{paymentId}/detail")
    public PaymentDetailAdminResponseDto getDetail(@PathVariable Long paymentId) {
        return adminPaymentService.getDetail(paymentId);
    }

    @PutMapping("/{paymentId}/refunded")
    public void refunded(@PathVariable Long paymentId) {
        adminPaymentService.refunded(paymentId);
    }
}
