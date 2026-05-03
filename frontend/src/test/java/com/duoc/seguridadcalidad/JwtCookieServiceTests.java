package com.duoc.seguridadcalidad;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class JwtCookieServiceTests {

    @Test
    void createAuthCookieShouldIncludeSecurityAttributes() {
        JwtCookieService service = new JwtCookieService(true, 3600, "Strict");

        ResponseCookie cookie = service.createAuthCookie("token123");

        assertEquals(JwtCookieService.COOKIE_NAME, cookie.getName());
        assertEquals("token123", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertTrue(cookie.isSecure());
        assertEquals("/", cookie.getPath());
        assertEquals(3600, cookie.getMaxAge().getSeconds());
        assertEquals("Strict", cookie.getSameSite());
    }

    @Test
    void clearAuthCookieShouldExpireCookie() {
        JwtCookieService service = new JwtCookieService(true, 3600, "Strict");

        ResponseCookie cookie = service.clearAuthCookie();

        assertEquals(JwtCookieService.COOKIE_NAME, cookie.getName());
        assertEquals("", cookie.getValue());
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }

    @Test
    void extractTokenShouldPreferAuthorizationHeader() {
        JwtCookieService service = new JwtCookieService(true, 3600, "Strict");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer abc");
        request.setCookies(new Cookie(JwtCookieService.COOKIE_NAME, "cookie-token"));

        assertEquals("abc", service.extractToken(request));
    }

    @Test
    void extractTokenShouldReturnNullWhenNoHeaderAndNoCookies() {
        JwtCookieService service = new JwtCookieService(true, 3600, "Strict");
        MockHttpServletRequest request = new MockHttpServletRequest();

        assertNull(service.extractToken(request));
    }

    @Test
    void extractTokenShouldReadCookieWhenHeaderMissing() {
        JwtCookieService service = new JwtCookieService(true, 3600, "Strict");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("OTHER", "x"), new Cookie(JwtCookieService.COOKIE_NAME, "cookie-token"));

        assertEquals("cookie-token", service.extractToken(request));
    }

    @Test
    void extractTokenShouldIgnoreBlankCookieValue() {
        JwtCookieService service = new JwtCookieService(true, 3600, "Strict");
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie(JwtCookieService.COOKIE_NAME, "   "));

        assertNull(service.extractToken(request));
    }
}
