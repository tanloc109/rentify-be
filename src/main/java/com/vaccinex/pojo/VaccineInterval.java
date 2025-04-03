package com.vaccinex.pojo;

import com.vaccinex.pojo.composite.VaccineIntervalId;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class VaccineInterval {

    @EmbeddedId
    VaccineIntervalId id;

    @ManyToOne
    @JoinColumn(name = "from_vaccine_id", nullable = false)
    @MapsId("fromVaccineId")
    Vaccine fromVaccine;

    @ManyToOne
    @JoinColumn(name = "to_vaccine_id", nullable = false)
    @MapsId("toVaccineId")
    Vaccine toVaccine;

    long daysBetween;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        VaccineInterval that = (VaccineInterval) obj;
        return Objects.equals(id, that.id);
    }
}
