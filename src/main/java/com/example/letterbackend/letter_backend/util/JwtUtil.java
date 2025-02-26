package com.example.letterbackend.letter_backend.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret:}")
    private String secretKeyFromYaml;

    private static final long EXPIRATION_TIME = 86400000; // 1일 (24시간)

    // 🔹 실제 사용할 시크릿 키 (환경 변수 우선)
    private String getSecretKey() {
        String envSecret = System.getenv("JWT_SECRET");
        return (envSecret != null && !envSecret.isEmpty()) ? envSecret : secretKeyFromYaml;
    }

    // 🔹 JWT 토큰 생성
    public String generateToken(String username) {
        return JWT.create()
                .withIssuer("letter-backend") // ✅ Issuer 추가
                .withSubject(username)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(getSecretKey()));
    }

    // 🔹 JWT 토큰 검증
    public String validateToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            System.err.println("⚠️ JWT 검증 실패: 토큰이 존재하지 않음");
            return null;
        }

        try {
            String username = JWT.require(Algorithm.HMAC256(getSecretKey()))
                    .withIssuer("letter-backend") // ✅ Issuer 검증 추가
                    .build()
                    .verify(token)
                    .getSubject();

            System.out.println("✅ JWT 검증 성공: " + username);
            return username;
        } catch (JWTVerificationException e) {
            System.err.println("❌ JWT 검증 실패: " + e.getMessage());
            return null;
        }
    }
}
