package com.vaccinex.thirdparty.payment;

import com.sba301.vaccinex.dto.internal.ObjectResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/vn-pay")
    public ResponseEntity<ObjectResponse> pay(HttpServletRequest request) {
        ObjectResponse response = ObjectResponse.builder()
                .status(HttpStatus.OK.toString())
                .message("Success")
                .data(paymentService.createVnPayPayment(request))
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/vn-pay-callback")
    public void payCallbackHandler(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            paymentService.handleVNPayCallback(request);
            response.sendRedirect("https://vaccinex.theanh0804.duckdns.org/hoan-tat-thanh-toan?status=success");
        } catch (Exception e) {
            String encodedMessage = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            response.sendRedirect("https://vaccinex.theanh0804.duckdns.org/hoan-tat-thanh-toan?status=error&message=" + encodedMessage);
        }
    }
}
