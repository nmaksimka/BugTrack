package io.github.nmaksimka.bugtrack.user;

import io.github.nmaksimka.bugtrack.common.DomainException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final UserRepository users;
    private final PasswordEncoder passwordEncoder;

    public RegistrationService(UserRepository users, PasswordEncoder passwordEncoder) {
        this.users = users;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User register(String name, String email, String password) {
        String normalizedEmail = User.normalize(email);
        if (users.existsByEmail(normalizedEmail)) {
            throw new DomainException("Пользователь с такой почтой уже зарегистрирован");
        }
        return users.save(new User(name.trim(), normalizedEmail, passwordEncoder.encode(password)));
    }
}
