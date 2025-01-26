package com.home.microBanc.service;

import com.home.microBanc.dto.LoginRequest;
import com.home.microBanc.dto.UserRequest;
import com.home.microBanc.modal.Account;
import com.home.microBanc.modal.EmailData;
import com.home.microBanc.modal.PhoneData;
import com.home.microBanc.modal.Users;
import com.home.microBanc.providers.JwtTokenProvider;
import com.home.microBanc.repository.AccountRepository;
import com.home.microBanc.repository.EmailDataRepository;
import com.home.microBanc.repository.PhoneDataRepository;
import com.home.microBanc.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static com.home.microBanc.contants.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailDataRepository emailDataRepository;
    private final PhoneDataRepository phoneDataRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public Users addEmail(Long userId, String newEmail) {
        var user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + userId));

        if (emailDataRepository.existsByEmail(newEmail))
            throw new IllegalArgumentException("Email " + ALREADY_USE);
        var emailData = new EmailData();
        emailData.setEmail(newEmail);
        emailData.setUser(user);
        emailDataRepository.save(emailData);
        return user;
    }

    @Transactional
    public Users addPhone(Long userId, String newPhone) {
        var user = userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND + userId));
        if (phoneDataRepository.existsByPhone(newPhone))
            throw new IllegalArgumentException("Телефон  " + ALREADY_USE);
        var phoneData = new PhoneData();
        phoneData.setPhone(newPhone);
        phoneData.setUser(user);
        phoneDataRepository.save(phoneData);
        return user;
    }

    @Transactional
    public BigDecimal getAndRecalculateBalance(Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND + userId));
        var account = user.getAccount();
        if (account == null)
            throw new EntityNotFoundException(user.getName() + ACCOUNT_NOT_FOUND);
        var recalculatedBalance = recalculateBalance(account);
        account.setBalance(recalculatedBalance);
        accountRepository.save(account);
        return recalculatedBalance;
    }

    private BigDecimal recalculateBalance(Account account) {
        var now = LocalDateTime.now();
        var secondsSinceStart = java.time.Duration.between(account.getStartDate().toLocalDateTime(), now).getSeconds();
        var intervals = secondsSinceStart / 30;
        var maxBalance = account.getStartBalance().multiply(BigDecimal.valueOf(2.07));
        BigDecimal newBalance;
        if (Double.isInfinite(Math.pow(1.1, intervals)))
            newBalance = maxBalance;
        else
            newBalance = account.getStartBalance()
                    .multiply(BigDecimal.valueOf(Math.pow(1.1, intervals)));
        return newBalance.min(maxBalance);
    }

    @Transactional
    public Users registerUser(UserRequest userRequest) {
        if (emailDataRepository.existsByEmail(userRequest.getEmail()))
            throw new ObjectAlreadyExistsException("Email " + ALREADY_USE);
        if (phoneDataRepository.existsByPhone(userRequest.getPhone()))
            throw new ObjectAlreadyExistsException("Телефон " + ALREADY_USE);
        if (userRequest.getStartBalance().compareTo(BigDecimal.ZERO) < 0)
            throw new ObjectAlreadyExistsException(CANNOT_LESS);

        var user = new Users();
        user.setName(userRequest.getName());
        user.setDateOfBirth(userRequest.getDateOfBirth());
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        user = userRepository.save(user);
        var emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail(userRequest.getEmail());
        emailDataRepository.save(emailData);
        var phoneData = new PhoneData();
        phoneData.setUser(user);
        phoneData.setPhone(userRequest.getPhone());
        phoneDataRepository.save(phoneData);
        var account = new Account();
        account.setUser(user);
        account.setStartBalance(userRequest.getStartBalance());
        account.setBalance(BigDecimal.ZERO);
        account.setStartDate(new Timestamp(System.currentTimeMillis()));
        accountRepository.save(account);

        return user;
    }

    public String loginUser(LoginRequest loginRequest) {
        var user = userRepository.findByEmailOrPhone(loginRequest.getIdentifier());
        if (user.isEmpty())
            throw new EntityNotFoundException(WRONG_EMAIL_OR_PHONE);
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.get().getPassword()))
            throw new InvalidPasswordException(WRONG_PASSWORD);

        return jwtTokenProvider.createToken(user.get().getId().toString());
    }
}