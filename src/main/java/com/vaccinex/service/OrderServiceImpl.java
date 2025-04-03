package com.vaccinex.service;

import com.sba301.vaccinex.dto.request.OrderRequest;
import com.sba301.vaccinex.dto.response.OrderDetailResponseDTO;
import com.sba301.vaccinex.dto.response.OrderSummaryResponseDTO;
import com.sba301.vaccinex.dto.response.RevenueResponseDTO;
import com.sba301.vaccinex.dto.response.MonthlyRevenueDTO;
import com.sba301.vaccinex.exception.EntityNotFoundException;
import com.sba301.vaccinex.pojo.enums.VaccineScheduleStatus;
import com.sba301.vaccinex.thirdparty.payment.PaymentService;
import com.sba301.vaccinex.pojo.*;
import com.sba301.vaccinex.pojo.enums.OrderStatus;
import com.sba301.vaccinex.pojo.enums.ServiceType;
import com.sba301.vaccinex.repository.*;
import com.sba301.vaccinex.service.spec.*;
import com.sba301.vaccinex.thirdparty.refund.RefundTransaction;
import com.sba301.vaccinex.thirdparty.refund.RefundTransactionRepository;
import com.sba301.vaccinex.thirdparty.refund.VNPayRefundService;
import jakarta.ejb.Stateless;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.*;

@Stateless
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private final AccountService accountService;
    private final ChildrenService childrenService;
    private final ComboService comboService;
    private final VaccineService vaccineService;
    private final PaymentService paymentService;
    private final VNPayRefundService vnPayRefundService;
    private final VaccineScheduleService vaccineScheduleService;
    private final VaccineScheduleRepository vaccineScheduleRepository;
    private final PaymentRepository paymentRepository;
    private final RefundTransactionRepository refundTransactionRepository;

    @Override
    public Order findById(Integer id) {
        return orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
    }

    @Override
    public Order updateStatus(Integer id, OrderStatus status) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy đơn hàng"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    @Override //AnhNT
    public Object createOrder(OrderRequest request, HttpServletRequest http) {
        User customer = accountService.getUserById(request.getCustomerId());
        Child child = childrenService.getChildById(request.getChildId());
        List<VaccineSchedule> vaccineSchedules = vaccineScheduleRepository.findByStatusAndChildId(VaccineScheduleStatus.DRAFT, child.getId());
        double totalPrice;
        if (request.getServiceType() == ServiceType.COMBO) {
            List<Combo> combos = request.getIds().stream().map(comboService::getComboById).toList();
            totalPrice = combos.stream().mapToDouble(Combo::getPrice).sum();
        } else {
            List<Vaccine> vaccines = request.getIds().stream().map(vaccineService::getVaccineById).toList();
            totalPrice = vaccines.stream().mapToDouble(Vaccine::getPrice).sum();
        }
        totalPrice = totalPrice * 0.2; // Cọc 20%
        Order order = Order.builder()
                .bookDate(LocalDateTime.now())
                .startDate(vaccineSchedules.getFirst().getDate())
                .serviceType(request.getServiceType())
                .status(OrderStatus.CANCELLED)
                .totalPrice(totalPrice)
                .customer(customer)
                .child(child)
                .build();
        order = orderRepository.save(order);
        return paymentService.requestPayment(totalPrice, "NCB", order.getId(), http);
    }

    @Override
    public VNPayRefundService.RefundResponse refundOrder(Integer orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(OrderStatus.CANCELLED);
        return vnPayRefundService.refundForCustomer(findById(orderId));
    }

    @Override
    public double calculateRefundAmount(Integer orderId) {
        return vnPayRefundService.calculateRefundAmount(findById(orderId));
    }

    @Override
    public List<OrderDetailResponseDTO> getOrdersWithSchedulesByCustomerId(Integer customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);

        return orders.stream().map(OrderDetailResponseDTO::from).toList();
    }

    @Override
    public OrderSummaryResponseDTO getOrderSummary() {
        List<Order> orders = orderRepository.findAll();

        Integer total = orders.size();
        Integer paid = 0;
        Integer cancelled = 0;

        for (Order order : orders) {
            if(order.getStatus() == OrderStatus.CANCELLED) {
                cancelled++;
            } else if(order.getStatus() == OrderStatus.PAID) {
                paid++;
            }
        }

        // Tính toán sự thay đổi so với tháng trước
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        int totalCurrentMonth = 0;
        int totalLastMonth = 0;

        for (Order order : orders) {
            YearMonth orderMonth = YearMonth.from(order.getBookDate());
            if (orderMonth.equals(currentMonth)) {
                totalCurrentMonth++;
            } else if (orderMonth.equals(previousMonth)) {
                totalLastMonth++;
            }
        }

        // Tính phần trăm thay đổi
        Double changePercentage = totalLastMonth > 0 ?
                ((double) (totalCurrentMonth - totalLastMonth) / totalLastMonth) * 100 : 0.0;

        return OrderSummaryResponseDTO.builder()
                .total(total)
                .paid(paid)
                .cancelled(cancelled)
                .currentMonthTotal(totalCurrentMonth)
                .lastMonthTotal(totalLastMonth)
                .changePercentage(changePercentage)
                .build();
    }

    @Override
    public RevenueResponseDTO getRevenue() {
        List<Order> orders = orderRepository.findAll();
        List<Payment> payments = paymentRepository.findAll();
        List<RefundTransaction> refundTransactions = refundTransactionRepository.findAll();

        // Tổng doanh thu
        Double grossRevenue = payments.stream()
                .mapToDouble(Payment::getAmount)
                .sum();

        // Tổng tiền hoàn
        Double totalRefund = refundTransactions.stream()
                .mapToDouble(RefundTransaction::getAmount)
                .sum();

        // Doanh thu thực
        Double netRevenue = grossRevenue - totalRefund;

        // Số lượng đơn hàng
        Integer totalOrders = orders.size();

        // Tính doanh thu theo tháng hiện tại và tháng trước
        LocalDateTime now = LocalDateTime.now();
        YearMonth currentMonth = YearMonth.from(now);
        YearMonth previousMonth = currentMonth.minusMonths(1);

        // Doanh thu tháng hiện tại
        Double currentMonthRevenue = payments.stream()
                .filter(p -> YearMonth.from(p.getDate()).equals(currentMonth))
                .mapToDouble(Payment::getAmount)
                .sum();

        // Doanh thu tháng trước
        Double previousMonthRevenue = payments.stream()
                .filter(p -> YearMonth.from(p.getDate()).equals(previousMonth))
                .mapToDouble(Payment::getAmount)
                .sum();

        // Tính phần trăm thay đổi
        Double revenueChangePercentage = previousMonthRevenue > 0 ?
                ((currentMonthRevenue - previousMonthRevenue) / previousMonthRevenue) * 100 : 0.0;

        // Tính số đơn tháng hiện tại và tháng trước
        long currentMonthOrders = orders.stream()
                .filter(o -> YearMonth.from(o.getBookDate()).equals(currentMonth))
                .count();

        long previousMonthOrders = orders.stream()
                .filter(o -> YearMonth.from(o.getBookDate()).equals(previousMonth))
                .count();

        // Tính phần trăm thay đổi số đơn
        Double ordersChangePercentage = previousMonthOrders > 0 ?
                ((double)(currentMonthOrders - previousMonthOrders) / previousMonthOrders) * 100 : 0.0;

        // Lấy dữ liệu doanh thu 12 tháng
        List<MonthlyRevenueDTO> monthlyData = getMonthlyRevenueData();

        return RevenueResponseDTO.builder()
                .grossRevenue(grossRevenue)
                .netRevenue(netRevenue)
                .totalRefund(totalRefund)
                .totalOrders(totalOrders)
                .currentMonthRevenue(currentMonthRevenue)
                .previousMonthRevenue(previousMonthRevenue)
                .revenueChangePercentage(revenueChangePercentage)
                .currentMonthOrders((int)currentMonthOrders)
                .previousMonthOrders((int)previousMonthOrders)
                .ordersChangePercentage(ordersChangePercentage)
                .monthlyData(monthlyData)
                .build();
    }

    /**
     * Lấy dữ liệu doanh thu theo tháng trong 12 tháng gần nhất
     */
    private List<MonthlyRevenueDTO> getMonthlyRevenueData() {
        List<Payment> payments = paymentRepository.findAll();
        List<RefundTransaction> refundTransactions = refundTransactionRepository.findAll();

        List<MonthlyRevenueDTO> monthlyData = new ArrayList<>();
        YearMonth currentYearMonth = YearMonth.now(ZoneId.systemDefault());

        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy");

        for (int i = 0; i < 12; i++) {
            YearMonth yearMonth = currentYearMonth.minusMonths(i);

            // Định dạng tên tháng có năm
            String monthName = yearMonth.format(monthFormatter);

            Double monthRevenue = payments.stream()
                    .filter(p -> p.getDate() != null)
                    .filter(p -> YearMonth.from(p.getDate()).equals(yearMonth))
                    .mapToDouble(Payment::getAmount)
                    .sum();

            Double monthRefund = refundTransactions.stream()
                    .filter(r -> r.getRequestDate() != null)
                    .filter(r -> YearMonth.from(r.getRequestDate()).equals(yearMonth))
                    .mapToDouble(RefundTransaction::getAmount)
                    .sum();

            Double netRevenue = monthRevenue - monthRefund;

            monthlyData.add(0, MonthlyRevenueDTO.builder()
                    .month(monthName)
                    .value(netRevenue)
                    .grossRevenue(monthRevenue)
                    .refund(monthRefund)
                    .build());
        }

        return monthlyData;
    }
}