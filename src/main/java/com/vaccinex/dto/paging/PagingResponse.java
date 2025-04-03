package com.vaccinex.dto.paging;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Map;

@Data
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PagingResponse {
    String code;
    String message;
    int currentPage;
    int totalPages;
    int pageSize;
    long totalElements;
    String params;
    String[] sortingOrders;
    Map<String, String> filters;
    Object data;
}
