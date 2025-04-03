package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.OrderRequest;
import com.sba301.vaccinex.dto.response.OrderDetailResponseDTO;
import com.sba301.vaccinex.dto.response.OrderSummaryResponseDTO;
import com.sba301.vaccinex.dto.response.RevenueResponseDTO;
import com.sba301.vaccinex.pojo.Order;
import com.sba301.vaccinex.pojo.enums.OrderStatus;
import com.sba301.vaccinex.thirdparty.refund.VNPayRefundService;
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
