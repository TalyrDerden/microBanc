package com.home.microBanc.service;

import com.home.microBanc.dto.UserRequest;
import com.home.microBanc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DefaultUserCreator {

    private final UserRepository userRepository;
    private final UserService userService;

    @PostConstruct
    public void createDefaultUser() {
        if (!userRepository.existsByName("admin")) {
            var userRequest = new UserRequest();
            userRequest.setName("admin");
            userRequest.setPassword("password");
            userRequest.setEmail("admin");
            userRequest.setPhone("admin");
            userRequest.setDateOfBirth(LocalDate.now());
            userRequest.setStartBalance(new BigDecimal("10.00"));
            userService.registerUser(userRequest);
        }
    }
}
