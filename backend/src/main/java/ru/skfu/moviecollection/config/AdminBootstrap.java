package ru.skfu.moviecollection.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.skfu.moviecollection.entity.Role;
import ru.skfu.moviecollection.entity.User;
import ru.skfu.moviecollection.foundation.UserRepository;

@Component
public class AdminBootstrap implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminEmail;
    private final String adminPassword;

    public AdminBootstrap(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.admin.email:admin@movie.local}") String adminEmail,
            @Value("${app.admin.password:admin123}") String adminPassword
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminEmail = adminEmail;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) {
        userRepository.findByEmail(adminEmail).ifPresentOrElse(
                user -> {
                    if (user.getRole() != Role.ADMIN) {
                        user.changeRole(Role.ADMIN);
                        userRepository.save(user);
                    }
                },
                () -> userRepository.save(new User(
                        adminEmail,
                        passwordEncoder.encode(adminPassword),
                        Role.ADMIN
                ))
        );
    }
}
