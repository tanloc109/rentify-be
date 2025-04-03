package com.vaccinex.dto.paging;

import com.sba301.vaccinex.pojo.Transaction;
import jakarta.persistence.criteria.Predicate;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TransactionPagingResponse {
    Integer id;
    LocalDateTime date;
    Integer doctorId;
    String doctorName;
    List<BatchInfo> batches;

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class BatchInfo {
        Integer batchId;
        Integer quantityTaken;
        Integer remaining;
    }

    public static TransactionPagingResponse fromEntity(Transaction transaction) {
        return TransactionPagingResponse.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .doctorId(transaction.getDoctor().getId())
                .doctorName(transaction.getDoctor().getFullName())
                .batches(transaction.getBatchTransactions().stream().map(
                        bt -> BatchInfo.builder()
                                .batchId(bt.getBatch().getId())
                                .quantityTaken(bt.getQuantityTaken())
                                .remaining(bt.getRemaining())
                                .build()
                ).toList())
                .build();
    }

    public static Specification<Transaction> filterByFields(Map<String, String> params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            params.forEach((fieldName, value) -> {
                switch (fieldName) {
                    // Integer-based fields
                    case "id" -> predicates.add(cb.equal(root.get(fieldName), Integer.parseInt(value)));

                    case "doctorId" -> predicates.add(cb.equal(root.get("doctor").get("id"), Integer.parseInt(value)));

                    // String-based fields (LIKE query)
                    case "doctorName" -> {
                        Predicate firstNamePredicate = cb.like(cb.lower(root.get("doctor").get("firstName")), "%" + value.toLowerCase() + "%");
                        Predicate lastNamePredicate = cb.like(cb.lower(root.get("doctor").get("lastName")), "%" + value.toLowerCase() + "%");
                        predicates.add(cb.or(firstNamePredicate, lastNamePredicate));
                    }

                    // Date-based fields (greater than or equal)
                    case "date" -> {
                        LocalDateTime date = LocalDateTime.parse(value);
                        predicates.add(cb.greaterThanOrEqualTo(root.get("date"), date));
                    }
                }
            });

            predicates.add(cb.equal(root.get("deleted"), false));

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
