package com.vaccinex.thirdparty.refund;

import com.sba301.vaccinex.pojo.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RefundTransactionRepository extends JpaRepository<RefundTransaction, Long> {
    List<RefundTransaction> findByPayment(Payment payment);
    List<RefundTransaction> findBySuccess(boolean success);
}