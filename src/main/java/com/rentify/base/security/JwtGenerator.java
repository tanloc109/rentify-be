package com.rentify.base.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.rentify.base.config.AppConfig;
import jakarta.enterprise.context.Dependent;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Dependent
public class JwtGenerator {

    private static final String CREATE_JWT_FAILED = "JWT creation failed";
    private static final String INVALID_TOKEN = "Invalid token";

    private final Algorithm algorithm = Algorithm.HMAC256(AppConfig.getJWTSecretKey());

    public String generateToken(Map<String, String> payload) {
        String token;
        try {
            // Create the JWT
            JWTCreator.Builder jwtBuilder = JWT.create();
            jwtBuilder.withPayload(payload);
            jwtBuilder.withIssuer(AppConfig.getJWTIssuer());
            jwtBuilder.withExpiresAt(new Date(System.currentTimeMillis() + AppConfig.getJWTTimeToLive()));

            token = jwtBuilder.sign(algorithm);
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new ServerErrorException(CREATE_JWT_FAILED, Response.Status.INTERNAL_SERVER_ERROR, exception);
        }
        return token;
    }

    public Map<String, String> validateToken(String token) throws BadRequestException {
        DecodedJWT decodedJWT;
        try {
            JWTVerifier verifier = JWT.require(algorithm).withIssuer(AppConfig.getJWTIssuer()).build();
            decodedJWT = verifier.verify(token);
        } catch (JWTVerificationException exception){
            // Invalid signature/claims
            throw new BadRequestException(INVALID_TOKEN);
        }

        Map<String, String> result = new HashMap<>();
        for (var key : decodedJWT.getClaims().keySet()) {
            result.put(key, decodedJWT.getClaim(key).asString());
        }
        return result;
    }

}
