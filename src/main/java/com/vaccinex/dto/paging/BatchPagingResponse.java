package com.vaccinex.dto.paging;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vaccinex.pojo.Batch;
import com.vaccinex.pojo.Vaccine;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonFilter("dynamicFilter")
public class BatchPagingResponse {
    Integer id;
    String batchCode;
    Integer batchSize;
    Integer quantity;
    LocalDateTime imported;
    LocalDateTime manufactured;
    LocalDateTime expiration;
    String distributer;
    Integer vaccineId;
    String vaccineName;
    String vaccineDescription;
    String vaccineCode;
    String vaccineManufacturer;
    Double vaccinePrice;
    Long vaccineExpiresInDays;
    Integer vaccineMinAge;
    Integer vaccineMaxAge;
    Integer vaccineDose;

    public static BatchPagingResponse fromEntity(Batch b) {
        return BatchPagingResponse.builder()
                .id(b.getId())
                .batchCode(b.getBatchCode())
                .batchSize(b.getBatchSize())
                .quantity(b.getQuantity())
                .imported(b.getImported())
                .expiration(b.getExpiration())
                .manufactured(b.getManufactured())
                .distributer(b.getDistributer())
                .vaccineId(b.getVaccine().getId())
                .vaccineName(b.getVaccine().getName())
                .vaccineDescription(b.getVaccine().getDescription())
                .vaccineCode(b.getVaccine().getVaccineCode())
                .vaccineManufacturer(b.getVaccine().getManufacturer())
                .vaccinePrice(b.getVaccine().getPrice())
                .vaccineExpiresInDays(b.getVaccine().getExpiresInDays())
                .vaccineMinAge(b.getVaccine().getMinAge())
                .vaccineMaxAge(b.getVaccine().getMaxAge())
                .vaccineDose(b.getVaccine().getDose())
                .build();
    }

    public static class BatchFilter {
        @PersistenceContext
        private EntityManager entityManager;

        public List<Batch> filterBatches(Map<String, String> params) {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<Batch> query = cb.createQuery(Batch.class);
            Root<Batch> root = query.from(Batch.class);
            Root<Vaccine> vaccineRoot = query.from(Vaccine.class);

            List<Predicate> predicates = new ArrayList<>();

            params.forEach((fieldName, value) -> {
                try {
                    switch (fieldName) {
                        // Integer-based fields
                        case "id" -> predicates.add(cb.equal(root.get("id"), Integer.parseInt(value)));
                        case "batchSize" -> predicates.add(cb.equal(root.get("batchSize"), Integer.parseInt(value)));
                        case "quantity" -> predicates.add(cb.equal(root.get("quantity"), Integer.parseInt(value)));
                        case "vaccineId" -> predicates.add(cb.equal(root.get("vaccine").get("id"), Integer.parseInt(value)));
                        case "batchCode" -> predicates.add(cb.like(cb.lower(root.get("batchCode")),
                                "%" + value.toLowerCase() + "%"));
                        case "distributer" -> predicates.add(cb.like(cb.lower(root.get("distributer")),
                                "%" + value.toLowerCase() + "%"));
                        case "vaccineName" -> predicates.add(cb.like(cb.lower(root.get("vaccine").get("name")),
                                "%" + value.toLowerCase() + "%"));
                        case "vaccineDescription" -> predicates.add(cb.like(cb.lower(root.get("vaccine").get("description")),
                                "%" + value.toLowerCase() + "%"));
                        case "vaccineCode" -> predicates.add(cb.like(cb.lower(root.get("vaccine").get("vaccineCode")),
                                "%" + value.toLowerCase() + "%"));
                        case "vaccineManufacturer" -> predicates.add(cb.like(cb.lower(root.get("vaccine").get("manufacturer")),
                                "%" + value.toLowerCase() + "%"));
                        case "imported" -> predicates.add(cb.greaterThanOrEqualTo(root.get("imported"),
                                LocalDateTime.parse(value)));
                        case "expiration" -> predicates.add(cb.greaterThanOrEqualTo(root.get("expiration"),
                                LocalDateTime.parse(value)));
                        case "manufactured" -> predicates.add(cb.greaterThanOrEqualTo(root.get("manufactured"),
                                LocalDateTime.parse(value)));
                    }
                } catch (NumberFormatException | DateTimeParseException e) {
                    // Log or handle parsing exceptions
                }
            });

            // Default filter for non-deleted batches
            predicates.add(cb.isFalse(root.get("deleted")));

            // Join condition to ensure batch is associated with vaccine
            predicates.add(cb.equal(root.get("vaccine"), vaccineRoot));

            query.where(predicates.toArray(new Predicate[0]));

            return entityManager.createQuery(query).getResultList();
        }
    }
}