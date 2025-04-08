package com.vaccinex.thirdparty.payment;

import com.vaccinex.dto.response.ObjectResponse;
import jakarta.annotation.security.PermitAll;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Path("/payment")
@RequiredArgsConstructor
public class PaymentController {

    @Inject
    private PaymentService paymentService;

    @GET
    @Path("/vn-pay")
    @PermitAll
    public Response pay(@Context HttpServletRequest request) {
        ObjectResponse response = ObjectResponse.builder()
                .status("200 OK")
                .message("Success")
                .data(paymentService.createVnPayPayment(request))
                .build();
        return Response.ok(response).build();
    }

    @GET
    @Path("/vn-pay-callback")
    @PermitAll
    public void payCallbackHandler(@Context HttpServletRequest request,
                                   @Context HttpServletResponse response) throws IOException {
        try {
            paymentService.handleVNPayCallback(request);
            response.sendRedirect("http://localhost:5173/hoan-tat-thanh-toan?status=success");
        } catch (Exception e) {
            String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("http://localhost:5173/hoan-tat-thanh-toan?status=error&message=" + encodedMessage);
        }
    }
}