package com.vaccinex.thirdparty.payment;

import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.dao.OrderDao;
import com.vaccinex.dao.PaymentDao;
import com.vaccinex.pojo.Order;
import com.vaccinex.pojo.Payment;
import com.vaccinex.pojo.enums.PaymentMethod;
import com.vaccinex.service.VaccineScheduleService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Stateless
public class PaymentService {


    @Inject
    private VNPAYConfig vnPayConfig;

    @Inject
    private PaymentDao paymentRepository;

    @Inject
    private OrderDao orderRepository;

    @Inject
    private VaccineScheduleService vaccineScheduleService;

    public PaymentDTO.VNPayResponse requestPayment(
            Double amount,
            String bankCode,
            Integer orderId,
            HttpServletRequest request
    ) {
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();

        System.out.println("Amount: " + amount);

        long amountInVND = Math.round(amount * 100);
        vnpParamsMap.put("vnp_Amount", String.valueOf(amountInVND));

//        if (bankCode != null && !bankCode.isEmpty()) {
//            vnpParamsMap.put("vnp_BankCode", bankCode);
//        }

        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));

        vnpParamsMap.put("vnp_OrderInfo", orderId.toString());

        return getVnPayResponse(vnpParamsMap);
    }

    public void handleVNPayCallback(HttpServletRequest request) {
        String responseCode = request.getParameter("vnp_ResponseCode");
        System.out.println("responseCode = " + responseCode);
        String txnRef = request.getParameter("vnp_TxnRef");
        String orderInfo = request.getParameter("vnp_OrderInfo");
        String transactionNo = request.getParameter("vnp_TransactionNo");
        System.out.println("txnRef = " + txnRef);
        //Thanh toán thất bại
        if (!"00".equals(responseCode)) {
            throw new BadRequestException("Thanh toán không thành công với mã phản hồi:" + responseCode);
        }

        Integer orderId = Integer.parseInt(request.getParameter("vnp_OrderInfo"));
        System.out.println("orderId = " + orderId);
        Order order = orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestException("Không tìm thấy lệnh với txnRef: " + txnRef)
        );
            String amount = request.getParameter("vnp_Amount");
        System.out.println("amount = " + amount);
        // Thanh toán thành công
        paymentRepository.save(Payment.builder()
                .paymentMethod(PaymentMethod.VNPAY)
                .date(LocalDateTime.now())
                .amount(Double.parseDouble(amount) / 100)
                .customer(order.getCustomer())
                .vnpTransactionNo(transactionNo)
                .vnpTxnRef(request.getParameter("vnp_TxnRef"))
                .order(order)
                .build());

        vaccineScheduleService.handleCallback(orderId);
    }

    //Demo
    private PaymentDTO.VNPayResponse getVnPayResponse(Map<String, String> vnpParamsMap) {
        String queryUrl = VNPayUtil.getPaymentURL(vnpParamsMap, true);
        String hashData = VNPayUtil.getPaymentURL(vnpParamsMap, false);
        String vnpSecureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        queryUrl += "&vnp_SecureHash=" + vnpSecureHash;
        String paymentUrl = vnPayConfig.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentDTO.VNPayResponse.builder()
                .code("ok")
                .message("success")
                .paymentUrl(paymentUrl)
                .build();
    }


    public PaymentDTO.VNPayResponse createVnPayPayment(HttpServletRequest request) {
        long amount = Integer.parseInt(request.getParameter("amount")) * 100L;
        String bankCode = request.getParameter("bankCode");
        Map<String, String> vnpParamsMap = vnPayConfig.getVNPayConfig();
        vnpParamsMap.put("vnp_Amount", String.valueOf(amount));
        if (bankCode != null && !bankCode.isEmpty()) {
            vnpParamsMap.put("vnp_BankCode", bankCode);
        }
        vnpParamsMap.put("vnp_IpAddr", VNPayUtil.getIpAddress(request));
        // Thêm orderId vào orderInfo
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + 3);
        //build query url
        return getVnPayResponse(vnpParamsMap);
    }
}
