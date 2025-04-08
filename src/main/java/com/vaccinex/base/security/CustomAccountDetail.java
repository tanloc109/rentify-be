package com.vaccinex.base.security;

import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.User;
import jakarta.security.enterprise.identitystore.CredentialValidationResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.Principal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomAccountDetail implements Principal {
    private Integer id;
    private String email;
    private String firstName;
    private String lastName;
    private Role role;
    
    @Override
    public String getName() {
        return email;
    }
    
    public static CustomAccountDetail mapAccountToAccountDetail(User user) {
        if (user == null) {
            return null;
        }
        
        return CustomAccountDetail.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
    
    public static CustomAccountDetail fromResult(CredentialValidationResult result, User user) {
        return CustomAccountDetail.builder()
                .id(user.getId())
                .email(result.getCallerPrincipal().getName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole())
                .build();
    }
}