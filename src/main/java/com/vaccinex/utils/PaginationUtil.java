package com.vaccinex.utils;

import com.vaccinex.dto.paging.PagingRequest;
import com.vaccinex.dto.paging.PagingResponse;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyFilter;
import jakarta.ws.rs.core.Response;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtil {

    public static class Pageable {
        private int pageNumber;
        private int pageSize;
        private List<SortOrder> sortOrders;

        public Pageable(int pageNumber, int pageSize, List<SortOrder> sortOrders) {
            this.pageNumber = pageNumber;
            this.pageSize = pageSize;
            this.sortOrders = sortOrders;
        }

        // Getters and setters
        public int getPageNumber() { return pageNumber; }
        public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
        public int getPageSize() { return pageSize; }
        public void setPageSize(int pageSize) { this.pageSize = pageSize; }
        public List<SortOrder> getSortOrders() { return sortOrders; }
        public void setSortOrders(List<SortOrder> sortOrders) { this.sortOrders = sortOrders; }
    }

    public static class SortOrder {
        private String field;
        private boolean ascending;

        public SortOrder(String field, boolean ascending) {
            this.field = field;
            this.ascending = ascending;
        }

        // Getters and setters
        public String getField() { return field; }
        public void setField(String field) { this.field = field; }
        public boolean isAscending() { return ascending; }
        public void setAscending(boolean ascending) { this.ascending = ascending; }
    }

    public static Pageable getPageable(PagingRequest request) {
        String[] sorts = request.getSortBy().split(",");
        List<SortOrder> sortingOrders = new ArrayList<>();

        for (String sortBy : sorts) {
            String[] parts = sortBy.split(":");
            String fieldName = parts[0];
            boolean isDescending = parts.length > 1 && parts[1].equalsIgnoreCase("desc");
            sortingOrders.add(new SortOrder(fieldName, !isDescending));
        }

        return request.getPageSize() > 0
                ? new Pageable(request.getPageNo() - 1, request.getPageSize(), sortingOrders)
                : new Pageable(0, Integer.MAX_VALUE, sortingOrders);
    }

    public static String getPagedJsonResponse(PagingRequest request, List<?> data, long totalElements, int totalPages) {
        PagingResponse pagingResponse = PagingResponse.builder()
                .code(String.valueOf(Response.Status.OK.getStatusCode()))
                .message("Successful retrieval")
                .currentPage(request.getPageNo())
                .totalElements(totalElements)
                .pageSize(request.getPageSize())
                .totalPages(totalPages)
                .sortingOrders(request.getSortBy().split(","))
                .params(request.getParams() != null ? request.getParams() : "All")
                .filters(request.getFilters())
                .data(data)
                .build();

        JsonbConfig config = new JsonbConfig();

        // If specific parameters are requested, filter them
        if (request.getParams() != null && !request.getParams().isBlank()) {
            String[] params = request.getParams().replaceAll(" ", "").split(",");
            config.withPropertyFilter(new PropertyFilter() {
                @Override
                public boolean shouldSerialize(String propName) {
                    for (String param : params) {
                        if (param.equalsIgnoreCase(propName)) {
                            return true;
                        }
                    }
                    return false;
                }
            });
        }

        Jsonb jsonb = JsonbBuilder.create(config);
        return jsonb.toJson(pagingResponse);
    }
}