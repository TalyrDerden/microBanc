package com.home.microBanc.controller;

import com.home.microBanc.dto.LoginRequest;
import com.home.microBanc.dto.UserRequest;
import com.home.microBanc.dto.UserSearchDTO;
import com.home.microBanc.repository.UserRepository;
import com.home.microBanc.service.SecurityService;
import com.home.microBanc.service.TransactionService;
import com.home.microBanc.service.UserService;
import com.home.microBanc.service.UserSpecification;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final TransactionService transactionService;
    private final SecurityService securityService;

    @Operation(summary = "Создание нового пользователя", description = "Позволяет создать нового пользователя в системе.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserRequest.class))),
            @ApiResponse(responseCode = "400", description = "Некорректные данные",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRequest userRequest) {
        try {
            var user = userService.registerUser(userRequest);
            return ResponseEntity.ok("Пользователь успешно создан!" + "\n" + user);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @Operation(summary = "Получение токена", description = "Позволяет получить токен для аутентификации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токен успешно получен",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginRequest.class))),
            @ApiResponse(responseCode = "400", description = "Неверные учетные данные",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            var token = userService.loginUser(loginRequest);
            return ResponseEntity.ok("token : " + token);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @Operation(summary = "Добавление почты пользователя", description = "Позволяет добавить почту пользователя.")
    @PreAuthorize("@securityService.isCurrentUser(#userId)")
    @PutMapping("/{userId}/email")
    public ResponseEntity<?> addEmail(@PathVariable Long userId, @RequestBody String newEmail) {
        try {
            var updatedUser = userService.addEmail(userId, newEmail);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @Operation(summary = "Добавление телефона пользователя", description = "Позволяет добавить телефон пользователя.")
    @PreAuthorize("@securityService.isCurrentUser(#userId)")
    @PutMapping("/{userId}/phone")
    public ResponseEntity<?> addPhone(@PathVariable Long userId, @RequestBody String newPhone) {
        try {
            var updatedUser = userService.addPhone(userId, newPhone);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @Operation(summary = "Поиск пользователя", description = "Позволяет найти пользователя.")
    @PostMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestBody UserSearchDTO searchDTO) {
        try {
            var pageRequest = PageRequest.of(searchDTO.getPage(), searchDTO.getSize());
            var spec = UserSpecification.searchUsers(searchDTO);
            var page = userRepository.findAll(spec, pageRequest);
            return ResponseEntity.ok(page);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }

    @Operation(summary = "Запрос баланса пользователя по ID", description = "Позволяет запросить баланс пользователя.")
    @GetMapping("/{id}/balance")
    public ResponseEntity<?> getBalance(@PathVariable Long id) {
        try {
            var balance = userService.getAndRecalculateBalance(id);
            return ResponseEntity.ok(balance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }


    @Operation(summary = "Трансфер денег между пользователями", description = "Позволяет переносить деньги между пользователями.")
    @PostMapping("/transfer")
    public ResponseEntity<?> transferMoney(
            @RequestParam Long recipientId,
            @RequestParam BigDecimal amount) {
        try {
            var user = securityService.curentUserId();
            Long senderId = null;
            if (user != null)
                senderId = user.get().getId();
            transactionService.transfer(senderId, recipientId, amount);
            return ResponseEntity.ok("Transfer successful");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getLocalizedMessage());
        }
    }
}