package com.vaccinex.base.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.vaccinex.base.config.AppConfig;
import com.vaccinex.base.contants.ApplicationMessage;
import com.vaccinex.base.exception.UnauthorizedException;
import com.vaccinex.pojo.enums.EnumTokenType;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class JwtGenerator {

    private final Algorithm algorithm = Algorithm.HMAC256(AppConfig.getJWTSecretKey());

    public String generateToken(Map<String, String> payload) {
        String token;
        try {
            JWTCreator.Builder jwtBuilder = JWT.create();
            jwtBuilder.withPayload(payload);
            jwtBuilder.withIssuer(AppConfig.getJWTIssuer());
            jwtBuilder.withExpiresAt(new Date(System.currentTimeMillis() + AppConfig.getJWTTimeToLive()));

            token = jwtBuilder.sign(algorithm);
        } catch (JWTCreationException exception){
            throw new ServerErrorException(ApplicationMessage.GENERATE_FAILED, Response.Status.INTERNAL_SERVER_ERROR, exception);
        }
        return token;
    }

    public Map<String, String> validateToken(String token) throws UnauthorizedException {
        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(AppConfig.getJWTIssuer())
                    .build();
            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception) {
            throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
        }

        Map<String, String> result = new HashMap<>();
        decodedJWT.getClaims().forEach((key, claim) -> result.put(key, claim.asString()));

        return result;
    }

    public String getEmailFromJwt(String token, EnumTokenType enumTokenType) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(AppConfig.getJWTIssuer())
                    .build();
            DecodedJWT decodedJWT = verifier.verify(token);
            return decodedJWT.getClaim("email").asString();
        } catch (JWTVerificationException exception) {
            throw new UnauthorizedException(ApplicationMessage.INVALID_TOKEN);
        }
    }

    public boolean validate(String token, EnumTokenType tokenType) {
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(AppConfig.getJWTIssuer())
                    .build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    public String generatedToken(CustomAccountDetail accountDetail) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", accountDetail.getEmail());
        payload.put("role", accountDetail.getRole().getRoleName().name());
        payload.put("firstName", accountDetail.getFirstName());
        payload.put("lastName", accountDetail.getLastName());
        payload.put("id", String.valueOf(accountDetail.getId()));
        return generateToken(payload);
    }

    public String generatedRefreshToken(CustomAccountDetail accountDetail) {
        Map<String, String> payload = new HashMap<>();
        payload.put("email", accountDetail.getEmail());
        payload.put("role", accountDetail.getRole().getRoleName().name());
        payload.put("firstName", accountDetail.getFirstName());
        payload.put("lastName", accountDetail.getLastName());
        payload.put("id", String.valueOf(accountDetail.getId()));
        String refreshToken;
        try {
            JWTCreator.Builder jwtBuilder = JWT.create();
            jwtBuilder.withPayload(payload);
            jwtBuilder.withIssuer(AppConfig.getJWTIssuer());
            // Thời gian sống dài hơn cho refresh token
            jwtBuilder.withExpiresAt(new Date(System.currentTimeMillis() + AppConfig.getJWTTimeToLive()));

            refreshToken = jwtBuilder.sign(algorithm);
        } catch (JWTCreationException exception){
            throw new ServerErrorException(ApplicationMessage.GENERATE_FAILED, Response.Status.INTERNAL_SERVER_ERROR, exception);
        }
        return refreshToken;
    }
}
