package com.vaccinex.dto.request;

import com.sba301.vaccinex.pojo.enums.Gender;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChildrenRequestDTO {
    @NotBlank(message = "Tên không được để trống")
    @Size(min = 1, max = 50, message = "Tên phải có độ dài từ 1 đến 50 ký tự")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Tên chỉ được chứa chữ cái, khoảng trắng và dấu câu cơ bản")
    String firstName;

    @NotBlank(message = "Họ không được để trống")
    @Size(min = 1, max = 50, message = "Họ phải có độ dài từ 1 đến 50 ký tự")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Họ chỉ được chứa chữ cái, khoảng trắng và dấu câu cơ bản")
    String lastName;

    @NotNull(message = "Ngày sinh không được để trống")
    @Past(message = "Ngày sinh phải là ngày trong quá khứ")
    LocalDate dob;

    @AssertTrue(message = "Trẻ phải trong độ tuổi từ 0 đến 18")
    private boolean isValidChildAge() {
        if (dob == null) {
            return true;
        }
        LocalDate now = LocalDate.now();
        LocalDate minDate = now.minusYears(18);
        return !dob.isBefore(minDate) && !dob.isAfter(now);
    }

    @NotNull(message = "Giới tính không được để trống")
    Gender gender;

    @NotNull(message = "Cân nặng không được để trống")
    @DecimalMin(value = "0.5", message = "Cân nặng phải từ 0.5 kg trở lên")
    @DecimalMax(value = "200.0", message = "Cân nặng không được vượt quá 200.0 kg")
    Double weight;

    @NotNull(message = "Chiều cao không được để trống")
    @DecimalMin(value = "30.0", message = "Chiều cao phải từ 30.0 cm trở lên")
    @DecimalMax(value = "200.0", message = "Chiều cao không được vượt quá 200.0 cm")
    Double height;

    @NotBlank(message = "Nhóm máu không được để trống")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Nhóm máu phải là một trong các loại: A+, A-, B+, B-, AB+, AB-, O+, O-")
    String bloodType;

    @NotBlank(message = "Ghi chú sức khỏe không được để trống")
    @Size(max = 300, message = "Ghi chú sức khỏe không được vượt quá 500 ký tự")
    @Size(min = 5, message = "Ghi chú sức khỏe không được dưới 5 ký tự")
    String healthNote;
}