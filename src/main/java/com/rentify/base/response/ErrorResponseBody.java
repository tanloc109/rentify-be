package com.rentify.base.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponseBody {
    private String message;
    private Map<String, String> errors;

    public static class ResponseBodyBuilder {
        private String message;
        private Map<String, String> errors;

        public ErrorResponseBody.ResponseBodyBuilder message(String message) {
            this.message = message;
            return this;
        }

        public ErrorResponseBody.ResponseBodyBuilder errors(Map<String, String> errors) {
            this.errors = errors;
            return this;
        }

        public ErrorResponseBody build() {
            ErrorResponseBody responseBody = new ErrorResponseBody();
            responseBody.message = this.message;
            responseBody.errors = this.errors;
            return responseBody;
        }
    }
}
