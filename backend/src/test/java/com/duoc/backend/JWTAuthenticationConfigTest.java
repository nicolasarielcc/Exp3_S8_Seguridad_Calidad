package com.duoc.backend;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.List;

import static com.duoc.backend.Constants.SUPER_SECRET_KEY;
import static com.duoc.backend.Constants.TOKEN_BEARER_PREFIX;
import static com.duoc.backend.Constants.getSigningKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JWTAuthenticationConfigTest {

    @Test
    void getJWTTokenShouldReturnBearerTokenWithExpectedClaims() {
        JWTAuthenticationConfig config = new JWTAuthenticationConfig();

        String bearer = config.getJWTToken("carlos");
        assertNotNull(bearer);
        assertTrue(bearer.startsWith(TOKEN_BEARER_PREFIX));

        String token = bearer.substring(TOKEN_BEARER_PREFIX.length());
        SecretKey key = (SecretKey) getSigningKey(SUPER_SECRET_KEY);
        Claims claims = Jwts.parser()
            .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("carlos", claims.getSubject());

        Object authorities = claims.get("authorities");
        assertNotNull(authorities);
        assertTrue(authorities instanceof List);
        @SuppressWarnings("unchecked")
        List<String> roles = (List<String>) authorities;
        assertTrue(roles.contains("ROLE_USER"));
    }
}
