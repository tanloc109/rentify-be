package com.vaccinex.thirdparty.refund;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaccinex.dao.PaymentDao;
import com.vaccinex.dao.VaccineScheduleDao;
import com.vaccinex.base.exception.BadRequestException;
import com.vaccinex.pojo.Order;
import com.vaccinex.pojo.Payment;
import com.vaccinex.pojo.VaccineSchedule;
import com.vaccinex.pojo.enums.VaccineScheduleStatus;
import com.vaccinex.thirdparty.payment.VNPAYConfig;
import com.vaccinex.thirdparty.payment.VNPayUtil;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.Builder;
import lombok.Data;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Stateless
public class VNPayRefundService {

    @Inject
    private VNPAYConfig vnPayConfig;

    @Inject
    private PaymentDao paymentRepository;

    @Inject
    private RefundTransactionDao refundTransactionRepository;

    @Inject
    private VaccineScheduleDao vaccineScheduleRepository;

    private Client client;

    @Inject
    private ObjectMapper objectMapper;

    public VNPayRefundService() {
        this.client = ClientBuilder.newClient();
        this.objectMapper = new ObjectMapper();
    }

    @Data
    @Builder
    public static class RefundResponse {
        private String responseId;
        private String responseCode;
        private String message;
        private String transactionNo;
        private String transactionStatus;
        private double amount;
        private boolean success;
    }

    /**
     * Process refund for a payment
     * @param paymentId The ID of the payment to refund
     * @param amount The amount to refund (partial or full)
     * @param refundReason The reason for refund
     * @param createdBy The user who initiated the refund
     * @return RefundResponse with the result of the refund operation
     */
    public RefundResponse processRefund(Integer paymentId, Double amount, String refundReason, String createdBy) {
        // 1. Find the payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment not found with ID: " + paymentId));

        // 2. Validate refund amount
        if (amount > payment.getAmount()) {
            throw new BadRequestException("Refund amount cannot exceed the original payment amount");
        }

        // 3. Prepare VNPay refund parameters
        Map<String, String> vnpParams = prepareRefundParams(payment, amount, refundReason, createdBy);

        // 4. Call VNPay API
        RefundResponse response = callVnPayRefundApi(vnpParams);

        System.out.println(response);

        // 5. Save refund transaction record
        saveRefundTransaction(payment, amount, refundReason, createdBy, response);

        return response;
    }

    public RefundResponse refundForCustomer(Order order) {
        double amount = 0;

        List<VaccineSchedule> bookings = order.getSchedules().stream()
                .filter(booking -> booking.getStatus() == VaccineScheduleStatus.PLANNED)
                .toList();

        if (bookings.isEmpty()) {
            throw new BadRequestException("No future appointments available for refund");
        }

        List<VaccineSchedule> updatedBookings = new ArrayList<>();

        for (VaccineSchedule booking: bookings) {
            if (booking.getStatus() == VaccineScheduleStatus.PLANNED) {
                long gapDays = Math.abs(ChronoUnit.DAYS.between(booking.getDate(), LocalDateTime.now()));
                if (gapDays < 7) {
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                    updatedBookings.add(booking);
                } else if (gapDays < 14) {
                    amount += ((booking.getVaccine().getPrice() / booking.getVaccine().getDose()) * 0.2) * 0.3;
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                    updatedBookings.add(booking);
                } else if (gapDays < 30) {
                    amount += ((booking.getVaccine().getPrice() / booking.getVaccine().getDose()) * 0.2) * 0.5;
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                    updatedBookings.add(booking);
                } else {
                    amount += ((booking.getVaccine().getPrice() / booking.getVaccine().getDose()) * 0.2) * 0.8;
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                    updatedBookings.add(booking);
                }
            }
        }
        System.out.println("Refund amount:" + amount);

        for (VaccineSchedule updatedBooking : updatedBookings) {
            vaccineScheduleRepository.save(updatedBooking);
        }

        return processRefund(order.getPayments().getFirst().getId(), amount, "CANCEL BOOKING", "VaccineX");
    }

    public double calculateRefundAmount(Order order) {
        double amount = 0;

        List<VaccineSchedule> bookings = order.getSchedules().stream()
                .filter(booking -> booking.getStatus() == VaccineScheduleStatus.PLANNED)
                .toList();

        if (bookings.isEmpty()) {
            throw new BadRequestException("No future appointments available for refund");
        }

        for (VaccineSchedule booking: bookings) {
            if (booking.getStatus() == VaccineScheduleStatus.PLANNED) {
                long gapDays = Math.abs(ChronoUnit.DAYS.between(booking.getDate(), LocalDateTime.now()));
                if (gapDays < 7) {
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                } else if (gapDays < 14) {
                    amount += ((booking.getVaccine().getPrice() / booking.getVaccine().getDose()) * 0.2) * 0.3;
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                } else if (gapDays < 30) {
                    amount += ((booking.getVaccine().getPrice() / booking.getVaccine().getDose()) * 0.2) * 0.5;
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                } else {
                    amount += ((booking.getVaccine().getPrice() / booking.getVaccine().getDose()) * 0.2) * 0.8;
                    booking.setStatus(VaccineScheduleStatus.CANCELLED);
                }
            }
        }

        return amount;
    }


    private Map<String, String> prepareRefundParams(Payment payment, Double amount, String refundReason, String createdBy) {
        Map<String, String> vnpParams = new HashMap<>();

        // Required parameters for refund command
        vnpParams.put("vnp_RequestId", VNPayUtil.getRandomNumber(32));
        vnpParams.put("vnp_Version", "2.1.0");
        vnpParams.put("vnp_Command", "refund");
        vnpParams.put("vnp_TmnCode", vnPayConfig.getVnp_TmnCode());

        // Determine if this is a full or partial refund
        boolean isFullRefund = Math.abs(amount - payment.getAmount()) < 0.01;
        vnpParams.put("vnp_TransactionType", isFullRefund ? "02" : "03");

        // Use the original payment reference
        vnpParams.put("vnp_TxnRef", payment.getVnpTxnRef());

        // Convert amount to VND (multiply by 100 as per VNPay requirements)
        long amountInVND = Math.round(amount * 100);
        vnpParams.put("vnp_Amount", String.valueOf(amountInVND));

        // Original transaction info
        if (payment.getVnpTransactionNo() != null) {
            vnpParams.put("vnp_TransactionNo", payment.getVnpTransactionNo());
        }

        // Format transaction date - use the original payment date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+7"));
        String transactionDate = sdf.format(java.sql.Timestamp.valueOf(payment.getDate()));
        vnpParams.put("vnp_TransactionDate", transactionDate);

        // User who created the refund
        vnpParams.put("vnp_CreateBy", createdBy);

        // Current date and time for the refund request
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+7"));
        String createDate = sdf.format(calendar.getTime());
        vnpParams.put("vnp_CreateDate", createDate);

        // Get server IP address
        try {
            String ipAddr = InetAddress.getLocalHost().getHostAddress();
            vnpParams.put("vnp_IpAddr", ipAddr);
        } catch (Exception e) {
            vnpParams.put("vnp_IpAddr", "127.0.0.1");
        }

        // Refund reason
        vnpParams.put("vnp_OrderInfo", refundReason);

        // Generate secure hash
        String hashData = createHashData(vnpParams);
        String secureHash = VNPayUtil.hmacSHA512(vnPayConfig.getSecretKey(), hashData);
        vnpParams.put("vnp_SecureHash", secureHash);

        return vnpParams;
    }

    private String createHashData(Map<String, String> vnpParams) {
        StringBuilder hashData = new StringBuilder();
        hashData.append(vnpParams.get("vnp_RequestId"));
        hashData.append("|").append(vnpParams.get("vnp_Version"));
        hashData.append("|").append(vnpParams.get("vnp_Command"));
        hashData.append("|").append(vnpParams.get("vnp_TmnCode"));
        hashData.append("|").append(vnpParams.get("vnp_TransactionType"));
        hashData.append("|").append(vnpParams.get("vnp_TxnRef"));
        hashData.append("|").append(vnpParams.get("vnp_Amount"));
        hashData.append("|").append(vnpParams.getOrDefault("vnp_TransactionNo", ""));
        hashData.append("|").append(vnpParams.get("vnp_TransactionDate"));
        hashData.append("|").append(vnpParams.get("vnp_CreateBy"));
        hashData.append("|").append(vnpParams.get("vnp_CreateDate"));
        hashData.append("|").append(vnpParams.get("vnp_IpAddr"));
        hashData.append("|").append(vnpParams.get("vnp_OrderInfo"));

        return hashData.toString();
    }

    private RefundResponse callVnPayRefundApi(Map<String, String> vnpParams) {
        try {
            // VNPAY API endpoint
            String apiUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";

            // Call the API using Jakarta RS Client
            Response response = client
                    .target(apiUrl)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(vnpParams, MediaType.APPLICATION_JSON));

            String responseBody = response.readEntity(String.class);

            // Parse the response
            Map<String, Object> responseMap = objectMapper.readValue(responseBody, Map.class);

            // Build and return the response object
            return RefundResponse.builder()
                    .responseId((String) responseMap.get("vnp_ResponseId"))
                    .responseCode((String) responseMap.get("vnp_ResponseCode"))
                    .message((String) responseMap.get("vnp_Message"))
                    .transactionNo((String) responseMap.get("vnp_TransactionNo"))
                    .transactionStatus((String) responseMap.get("vnp_TransactionStatus"))
                    .amount(Double.parseDouble((String) responseMap.get("vnp_Amount")) / 100)
                    .success("00".equals(responseMap.get("vnp_ResponseCode")))
                    .build();
        } catch (Exception e) {
            throw new BadRequestException("Error calling VNPay refund API: " + e.getMessage());
        }
    }

    private void saveRefundTransaction(Payment payment, Double amount, String refundReason,
                                       String createdBy, RefundResponse response) {
        RefundTransaction refundTx = RefundTransaction.builder()
                .payment(payment)
                .amount(amount)
                .reason(refundReason)
                .requestDate(LocalDateTime.now())
                .responseCode(response.getResponseCode())
                .responseMessage(response.getMessage())
                .transactionNo(response.getTransactionNo())
                .transactionStatus(response.getTransactionStatus())
                .createdBy(createdBy)
                .success(response.isSuccess())
                .build();

        refundTransactionRepository.save(refundTx);
    }
}