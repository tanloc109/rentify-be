package com.vaccinex.pojo.composite;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
@Builder
public class VaccineComboId implements Serializable {

    @Column(name = "vaccine_id", nullable = false)
    private Integer vaccineId;

    @Column(name = "combo_id", nullable = false)
    private Integer comboId;

    @Column(name = "order_in_combo", nullable = false)
    private Integer orderInCombo;
}
