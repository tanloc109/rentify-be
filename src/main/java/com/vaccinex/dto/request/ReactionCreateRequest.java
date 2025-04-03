package com.vaccinex.dto.request;

import lombok.Builder;

@Builder
public record ReactionCreateRequest(
        String reaction, String reportedBy
) {
}
