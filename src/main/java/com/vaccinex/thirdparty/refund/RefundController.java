package com.vaccinex.thirdparty.refund;

import com.vaccinex.dto.internal.ObjectResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payment/refund")
@RequiredArgsConstructor
public class RefundController {

    private final VNPayRefundService vnPayRefundService;

    @Data
    public static class RefundRequest {
        private Integer paymentId;
        private Double amount;
        private String reason;
        private String refundedBy;
    }

    @PostMapping("/vnpay")
    public ResponseEntity<ObjectResponse> refundVnPayTransaction(@RequestBody RefundRequest request) {
        VNPayRefundService.RefundResponse refundResponse = vnPayRefundService.processRefund(
                request.getPaymentId(),
                request.getAmount(),
                request.getReason(),
                request.getRefundedBy()
        );

        String message = refundResponse.isSuccess() ?
                "Hoàn tiền đã được xử lý thành công" :
                "Yêu cầu hoàn tiền không thành công: " + refundResponse.getMessage();

        ObjectResponse response = ObjectResponse.builder()
                .status(refundResponse.isSuccess() ?
                        HttpStatus.OK.toString() :
                        HttpStatus.BAD_REQUEST.toString())
                .message(message)
                .data(refundResponse)
                .build();

        return new ResponseEntity<>(response,
                refundResponse.isSuccess() ? HttpStatus.OK : HttpStatus.BAD_REQUEST);
    }
}