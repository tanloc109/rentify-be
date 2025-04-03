package com.vaccinex.service;

import com.vaccinex.dto.request.OrderRequest;
import com.vaccinex.dto.response.OrderDetailResponseDTO;
import com.vaccinex.dto.response.OrderSummaryResponseDTO;
import com.vaccinex.dto.response.RevenueResponseDTO;
import com.vaccinex.pojo.Order;
import com.vaccinex.pojo.enums.OrderStatus;
import com.vaccinex.thirdparty.refund.VNPayRefundService;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface OrderService {
    Order findById(Integer id);

    Order updateStatus(Integer id, OrderStatus status);

    Object createOrder(OrderRequest request, HttpServletRequest http); //AnhNT

    VNPayRefundService.RefundResponse refundOrder(Integer orderId);

    double calculateRefundAmount(Integer orderId);

    List<OrderDetailResponseDTO> getOrdersWithSchedulesByCustomerId(Integer customerId);

    OrderSummaryResponseDTO getOrderSummary();

    RevenueResponseDTO getRevenue();
}
