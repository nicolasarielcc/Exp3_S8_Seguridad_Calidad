package com.duoc.backend;

import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConstantsTest {

    @Test
    void getSigningKeyShouldReturnUsableKey() {
        SecretKey key = (SecretKey) Constants.getSigningKey("this-is-a-long-enough-secret-for-hmac-key-material");
        assertNotNull(key);

        String token = Jwts.builder()
                .subject("u")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    @Test
    void getSigningKeyB64ShouldReturnUsableKeyForValidBase64() {
        String raw = "01234567890123456789012345678901"; // 32 bytes
        String b64 = Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
        SecretKey key = (SecretKey) Constants.getSigningKeyB64(b64);
        assertNotNull(key);

        String token = Jwts.builder()
                .subject("u")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(key)
                .compact();

        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}
