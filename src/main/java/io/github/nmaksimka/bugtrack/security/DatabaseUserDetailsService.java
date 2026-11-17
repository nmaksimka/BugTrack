package io.github.nmaksimka.bugtrack.security;

import io.github.nmaksimka.bugtrack.user.User;
import io.github.nmaksimka.bugtrack.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final UserRepository users;

    public DatabaseUserDetailsService(UserRepository users) {
        this.users = users;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        return users.findByEmail(User.normalize(email))
                .map(CurrentUser::from)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }
}
