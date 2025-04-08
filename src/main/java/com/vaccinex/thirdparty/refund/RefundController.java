package com.vaccinex.thirdparty.refund;

import com.vaccinex.dto.response.ObjectResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import lombok.Data;

@Path("/payment/refund")
public class RefundController {

    @Inject
    private VNPayRefundService vnPayRefundService;

    @Data
    public static class RefundRequest {
        private Integer paymentId;
        private Double amount;
        private String reason;
        private String refundedBy;
    }

    @POST
    @Path("/vnpay")
    @PermitAll
    public Response refundVnPayTransaction(@RequestBody RefundRequest request) {
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
                        Response.ok().toString() :
                        Response.status(Response.Status.BAD_REQUEST).toString())
                .message(message)
                .data(refundResponse)
                .build();

        return Response.status(refundResponse.isSuccess() ? Response.Status.OK : Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}