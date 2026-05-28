package ru.skfu.moviecollection.mediator;

import ru.skfu.moviecollection.control.dto.AuthRequest;
import ru.skfu.moviecollection.control.dto.AuthResponse;

public interface AuthService {
    AuthResponse register(AuthRequest request);

    AuthResponse login(AuthRequest request);
}

