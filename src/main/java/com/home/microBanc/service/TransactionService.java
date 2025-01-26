package com.home.microBanc.service;

import com.home.microBanc.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;

import static com.home.microBanc.contants.ErrorMessages.*;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final AccountRepository accountRepository;
    private final UserService userService;

    @Transactional
    public void transfer(Long senderId, Long recipientId, BigDecimal amount) {
        if (senderId == null)
            throw new IllegalArgumentException(USER_NOT_FOUND);
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException(TRANSFER_AMOUNT_MUST_BE_POSITIVE);
        if (senderId.equals(recipientId))
            throw new IllegalArgumentException(CANNOT_TRANSFER_TO_SAME_ACCOUNT);
        userService.getAndRecalculateBalance(senderId);
        userService.getAndRecalculateBalance(recipientId);
        var sender = accountRepository.findByUserId(senderId)
                .orElseThrow(() -> new EntityNotFoundException("Отправитель " + ACCOUNT_NOT_FOUND));
        var recipient = accountRepository.findByUserId(recipientId)
                .orElseThrow(() -> new EntityNotFoundException("Получатель " + ACCOUNT_NOT_FOUND));
        if (sender.getBalance().compareTo(amount) < 0)
            throw new IllegalArgumentException(INSUFFICIENT_FOUNDS);
        sender.setBalance(sender.getBalance().subtract(amount));
        recipient.setBalance(recipient.getBalance().add(amount));
        accountRepository.save(sender);
        accountRepository.save(recipient);
    }
}