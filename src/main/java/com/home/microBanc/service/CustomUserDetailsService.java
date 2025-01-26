package com.home.microBanc.service;

import com.home.microBanc.modal.Users;
import com.home.microBanc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static com.home.microBanc.contants.ErrorMessages.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        var user = userRepository.findByEmailOrPhone(identifier)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + identifier));

        return User.withUsername(identifier)
                .password(user.getPassword())
                .authorities("ROLE_USER")
                .build();
    }
}