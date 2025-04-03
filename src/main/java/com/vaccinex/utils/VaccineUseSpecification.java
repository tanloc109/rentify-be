package com.vaccinex.utils;

import com.sba301.vaccinex.pojo.VaccineUse;
import org.springframework.data.jpa.domain.Specification;

public class VaccineUseSpecification {

    public static Specification<VaccineUse> searchByField(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            if (field.equals("name")) {
                return criteriaBuilder.like(root.get("name"), "%" + value + "%");
            }
            return null;
        };
    }

}
