package ru.skfu.moviecollection.control;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import ru.skfu.moviecollection.control.dto.AuthRequest;
import ru.skfu.moviecollection.control.dto.AuthResponse;
import ru.skfu.moviecollection.entity.Role;
import ru.skfu.moviecollection.mediator.AuthService;

class AuthControllerTest {
    private final AuthService authService = mock(AuthService.class);
    private final AuthController controller = new AuthController(authService);

    @Test
    void registerDelegatesToAuthService() {
        var request = new AuthRequest("new@example.com", "123456");
        var response = new AuthResponse("token", UUID.randomUUID(), request.email(), Role.USER);
        when(authService.register(request)).thenReturn(response);

        var result = controller.register(request);

        assertEquals(response, result);
        verify(authService).register(request);
    }

    @Test
    void loginDelegatesToAuthService() {
        var request = new AuthRequest("test@yandex.ru", "123456");
        var response = new AuthResponse("token", UUID.randomUUID(), request.email(), Role.USER);
        when(authService.login(request)).thenReturn(response);

        var result = controller.login(request);

        assertEquals(response, result);
        verify(authService).login(request);
    }
}
