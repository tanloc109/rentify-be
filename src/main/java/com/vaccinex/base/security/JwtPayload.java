package com.vaccinex.base.security;

import com.vaccinex.pojo.Role;
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

    private String email;
    private Role role;

    public Map<String, String> toMap() {
        Map<String, String> result = new HashMap<>();
        result.put(EMAIL_KEY, email);
        result.put(ROLE_KEY, role.toString());

        return result;
    }

    public static JwtPayload fromMap(Map<String, String> map) {
        JwtPayload payload = new JwtPayload();
        payload.setEmail(map.get(EMAIL_KEY));
//        payload.setRole(Role.fromString(map.get(ROLE_KEY)));

        return payload;
    }

    @Override
    public String getName() {
        return email;
    }

    public static JwtPayload fromToken(String token, String secretKey) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey.getBytes())
                .parseClaimsJws(token.substring(7))
                .getBody();

//        return new JwtPayload(
//                claims.get(EMAIL_KEY, String.class),
//                Role.fromString(claims.get(ROLE_KEY, String.class))
//        );
        return null;
    }
}
