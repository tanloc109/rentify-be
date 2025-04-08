package com.vaccinex.base.security;

import com.vaccinex.pojo.Role;
import com.vaccinex.pojo.enums.EnumRoleNameType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JwtPayload implements Principal {
    private static final String EMAIL_KEY = "email";
    private static final String ROLE_KEY = "role";
    private static final String FIRST_NAME_KEY = "firstName";
    private static final String LAST_NAME_KEY = "lastName";
    private static final String ID_KEY = "id";

    private String email;
    private String role;
    private String firstName;
    private String lastName;
    private Integer id;

    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<>();
        result.put(EMAIL_KEY, email);
        result.put(ROLE_KEY, role);
        if (firstName != null) result.put(FIRST_NAME_KEY, firstName);
        if (lastName != null) result.put(LAST_NAME_KEY, lastName);
        if (id != null) result.put(ID_KEY, id.toString());

        return result;
    }

    public static JwtPayload fromMap(Map<String, String> map) {
        JwtPayload payload = new JwtPayload();
        payload.setEmail(map.get(EMAIL_KEY));
        payload.setRole(map.get(ROLE_KEY));
        payload.setFirstName(map.get(FIRST_NAME_KEY));
        payload.setLastName(map.get(LAST_NAME_KEY));

        String idStr = map.get(ID_KEY);
        if (idStr != null && !idStr.isEmpty()) {
            try {
                payload.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                // Ignoring ID if it's not a valid integer
            }
        }

        return payload;
    }

    @Override
    public String getName() {
        return email;
    }

    public static JwtPayload fromToken(String token, String secretKey) {
        if (token == null || token.isEmpty()) {
            return null;
        }

        // Remove "Bearer " prefix if present
        String tokenValue = token;
        if (token.startsWith("Bearer ")) {
            tokenValue = token.substring(7);
        }

        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey.getBytes())
                    .parseClaimsJws(tokenValue)
                    .getBody();

            JwtPayload payload = new JwtPayload();
            payload.setEmail(claims.get(EMAIL_KEY, String.class));
            payload.setRole(claims.get(ROLE_KEY, String.class));
            payload.setFirstName(claims.get(FIRST_NAME_KEY, String.class));
            payload.setLastName(claims.get(LAST_NAME_KEY, String.class));

            Object idObj = claims.get(ID_KEY);
            if (idObj != null) {
                if (idObj instanceof Integer) {
                    payload.setId((Integer) idObj);
                } else if (idObj instanceof String) {
                    try {
                        payload.setId(Integer.parseInt((String) idObj));
                    } catch (NumberFormatException e) {
                        // Ignoring ID if it's not a valid integer
                    }
                }
            }

            return payload;
        } catch (Exception e) {
            // Log error or handle appropriately
            return null;
        }
    }

    public EnumRoleNameType getRoleEnum() {
        try {
            return EnumRoleNameType.valueOf(role);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}