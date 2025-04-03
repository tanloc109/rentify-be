package com.vaccinex.pojo;

import com.vaccinex.pojo.enums.OrderStatus;
import com.vaccinex.pojo.enums.ServiceType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "vaccine_order")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    LocalDateTime bookDate;

    LocalDateTime startDate;

    @Enumerated(EnumType.STRING)
    ServiceType serviceType;

    @Enumerated(EnumType.STRING)
    OrderStatus status;

    Double totalPrice;

    @OneToMany(mappedBy = "order")
    List<Payment> payments;

    @ManyToOne
    User customer;

    @ManyToOne
    Child child;

    @OneToMany(mappedBy = "order")
    List<VaccineSchedule> schedules;

    @ManyToMany(mappedBy = "orders")
    List<Combo> combos;

}
