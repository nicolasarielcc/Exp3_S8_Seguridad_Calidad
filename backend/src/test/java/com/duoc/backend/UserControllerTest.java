package com.duoc.backend;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void registerUserShouldReturnCreatedWithUserResponse() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        request.setUsername("carlos");
        request.setEmail("carlos@duoc.cl");
        request.setPassword("secret");

        User saved = new User();
        saved.setId(10);
        saved.setUsername("carlos");
        saved.setEmail("carlos@duoc.cl");

        when(userService.registerUser(request)).thenReturn(saved);

        ResponseEntity<?> response = userController.registerUser(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertInstanceOf(UserResponse.class, response.getBody());

        UserResponse body = (UserResponse) response.getBody();
        assertEquals(10, body.getId());
        assertEquals("carlos", body.getUsername());
        assertEquals("carlos@duoc.cl", body.getEmail());
    }

    @Test
    void registerUserShouldReturnBadRequestWithMessageWhenServiceThrows() {
        UserRegistrationRequest request = new UserRegistrationRequest();
        when(userService.registerUser(request)).thenThrow(new IllegalArgumentException("Username already exists"));

        ResponseEntity<?> response = userController.registerUser(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertInstanceOf(Map.class, response.getBody());

        @SuppressWarnings("unchecked")
        Map<String, String> error = (Map<String, String>) response.getBody();
        assertEquals("Username already exists", error.get("message"));
    }
}
