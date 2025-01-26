package com.home.microBanc;

import com.home.microBanc.modal.Account;
import com.home.microBanc.repository.AccountRepository;
import com.home.microBanc.service.TransactionService;
import com.home.microBanc.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.Optional;

import static com.home.microBanc.contants.ErrorMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceMockTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private TransactionService transactionService;

    private Account sender;
    private Account recipient;
    private Long senderId = 1L;
    private Long recipientId = 2L;

    @BeforeEach
    void setUp() {
        sender = new Account();
        sender.setBalance(BigDecimal.valueOf(100));
        recipient = new Account();
        recipient.setBalance(BigDecimal.valueOf(50));
    }

    @Test
    void transfer_shouldTransferMoneySuccessfully() {
        var amount = BigDecimal.valueOf(50);
        when(accountRepository.findByUserId(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByUserId(recipientId)).thenReturn(Optional.of(recipient));
        transactionService.transfer(senderId, recipientId, amount);
        assertEquals(BigDecimal.valueOf(50), sender.getBalance());
        assertEquals(BigDecimal.valueOf(100), recipient.getBalance());
        verify(accountRepository).save(sender);
        verify(accountRepository).save(recipient);
    }

    @Test
    void transfer_shouldThrowExceptionWhenSenderNotFound() {
        var amount = BigDecimal.valueOf(50);
        when(accountRepository.findByUserId(senderId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.transfer(senderId, recipientId, amount));
        assertEquals("Отправитель " + ACCOUNT_NOT_FOUND, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowExceptionWhenRecipientNotFound() {
        var amount = BigDecimal.valueOf(50);
        when(accountRepository.findByUserId(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByUserId(recipientId)).thenReturn(Optional.empty());
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> transactionService.transfer(senderId, recipientId, amount));
        assertEquals("Получатель " + ACCOUNT_NOT_FOUND, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowExceptionWhenInsufficientFunds() {
        var amount = BigDecimal.valueOf(200);
        when(accountRepository.findByUserId(senderId)).thenReturn(Optional.of(sender));
        when(accountRepository.findByUserId(recipientId)).thenReturn(Optional.of(recipient));
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(senderId, recipientId, amount));
        assertEquals(INSUFFICIENT_FOUNDS, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowExceptionWhenTransferAmountIsNegative() {
        var amount = BigDecimal.valueOf(-50);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(senderId, recipientId, amount));
        assertEquals(TRANSFER_AMOUNT_MUST_BE_POSITIVE, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowExceptionWhenSenderIdIsNull() {
        Long senderNull = null;
        BigDecimal amount = BigDecimal.valueOf(50);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(senderNull, recipientId, amount));

        assertEquals(USER_NOT_FOUND, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }

    @Test
    void transfer_shouldThrowExceptionWhenSenderAndRecipientAreSame() {
        BigDecimal amount = BigDecimal.valueOf(50);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> transactionService.transfer(senderId, senderId, amount));
        assertEquals(CANNOT_TRANSFER_TO_SAME_ACCOUNT, exception.getMessage());
        verify(accountRepository, never()).save(any());
    }
}
