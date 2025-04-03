package com.vaccinex.utils;

import com.sba301.vaccinex.pojo.Vaccine;
import org.springframework.data.jpa.domain.Specification;

public class VaccineSpecification {

    public static Specification<Vaccine> searchByField(String field, String value) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || value.trim().isEmpty()) {
                return criteriaBuilder.conjunction();
            }

            switch (field) {
                case "name":
                    return criteriaBuilder.like(root.get("name"), "%" + value + "%");
                case "purpose":
                    return criteriaBuilder.like(root.join("uses").get("name"), "%" + value + "%");
                case "priceBegin":
                    try {
                        return criteriaBuilder.greaterThanOrEqualTo(root.get("price"), Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        return null;
                    }

                case "priceEnd":
                    try {
                        return criteriaBuilder.lessThanOrEqualTo(root.get("price"), Double.parseDouble(value));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                case "ageBegin":
                    try {
                        return criteriaBuilder.greaterThanOrEqualTo(root.get("minAge"), Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                case "ageEnd":
                    try {
                        return criteriaBuilder.lessThanOrEqualTo(root.get("maxAge"), Float.parseFloat(value));
                    } catch (NumberFormatException e) {
                        return null;
                    }
                case "ageRange":
                    try {
                        String[] ageRange = value.split(",");
                        if (ageRange.length == 2) {
                            int minAge = Integer.parseInt(ageRange[0]);
                            int maxAge = Integer.parseInt(ageRange[1]);
                            return criteriaBuilder.and(
                                    criteriaBuilder.greaterThanOrEqualTo(root.get("minAge"), minAge),
                                    criteriaBuilder.lessThanOrEqualTo(root.get("maxAge"), maxAge)
                            );
                        }
                    } catch (NumberFormatException e) {
                        return null;
                    }
                default:
                    return null;
            }
        };
    }

}
