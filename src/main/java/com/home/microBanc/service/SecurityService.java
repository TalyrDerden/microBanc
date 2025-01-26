package com.home.microBanc.service;

import com.home.microBanc.modal.Users;
import com.home.microBanc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SecurityService {
    private final UserRepository userRepository;

    public boolean isCurrentUser(Long userId) {
        var currentUser = curentUserId();
        return currentUser.map(users -> users.getId().equals(userId)).orElse(false);
    }

    public Optional<Users> curentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var currentUserName = authentication.getName();
        return userRepository.findByName(currentUserName);
    }
}
