package com.vaccinex.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AccountRegisterRequest {

    @Email(message = "Email không hợp lệ")
    @NotBlank(message = "Vui lòng nhập email")
    @Size(min = 10, max = 255, message = "Email phải từ 10 tới 255 kí tự bao gồm cả @gmail.com")
    private String email;

    @NotBlank(message = "Vui lòng nhập mật khẩu")
    @Size(min = 6, max = 100, message = "Mật khẩu phải từ 6 tới 100 kí tự")
    private String password;

    @NotBlank(message = "Vui lòng nhập tên")
    @Size(min = 2, max = 255, message = "Tên phải từ 2 tới 255 kí tự")
    private String firstName;

    @NotBlank(message = "Vui lòng nhập họ")
    @Size(min = 2, max = 255, message = "Họ phải từ 2 tới 255 kí tự")
    private String lastName;

    @NotBlank(message = "Vui lòng nhập số điện thoại")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Số điện thoại không hợp lệ (chỉ chứa số, từ 10-15 chữ số)")
    private String phone;

    @NotBlank(message = "Vui lòng nhập địa chỉ")
    @Size(min = 6, max = 255, message = "Địa chỉ phải từ 6 tới 255 kí tự")
    private String address;

    @PastOrPresent(message = "Ngày sinh không được ở tương lai")
    private LocalDate dob;
}
