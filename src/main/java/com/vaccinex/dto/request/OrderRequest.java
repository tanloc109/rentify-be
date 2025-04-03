package com.vaccinex.dto.request;

import com.vaccinex.pojo.enums.ServiceType;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderRequest {
    Integer customerId;
    Integer childId;
    ServiceType serviceType;
    List<Integer> ids;

}
