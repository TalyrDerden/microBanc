package com.home.microBanc;

import com.home.microBanc.modal.Account;
import com.home.microBanc.modal.EmailData;
import com.home.microBanc.modal.PhoneData;
import com.home.microBanc.modal.Users;
import com.home.microBanc.repository.AccountRepository;
import com.home.microBanc.repository.EmailDataRepository;
import com.home.microBanc.repository.PhoneDataRepository;
import com.home.microBanc.repository.UserRepository;
import com.home.microBanc.service.TransactionService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import static com.home.microBanc.contants.ErrorMessages.USER_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class TransactionServiceTest {

    @Container
    public static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EmailDataRepository emailDataRepository;
    @Autowired
    private PhoneDataRepository phoneDataRepository;
    @Autowired
    private AccountRepository accountRepository;

    private Account sender;
    private Account recipient;
    BigDecimal amount = BigDecimal.valueOf(100);


    @BeforeAll
    static void setUpBeforeClass() {
        postgreSQLContainer.start();
        System.setProperty("spring.datasource.url", postgreSQLContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgreSQLContainer.getUsername());
        System.setProperty("spring.datasource.password", postgreSQLContainer.getPassword());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    void createUsers() {
        var user = new Users();
        user.setName("one");
        user.setDateOfBirth(LocalDate.now());
        user.setPassword(passwordEncoder.encode("1"));
        user = userRepository.save(user);
        var emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail("one@mail.mail");
        emailDataRepository.save(emailData);
        var phoneData = new PhoneData();
        phoneData.setUser(user);
        phoneData.setPhone("1");
        phoneDataRepository.save(phoneData);
        sender = new Account();
        sender.setUser(user);
        sender.setStartBalance(BigDecimal.valueOf(500));
        sender.setBalance(BigDecimal.valueOf(500));
        sender.setStartDate(new Timestamp(System.currentTimeMillis()));
        accountRepository.save(sender);

        user.setAccount(sender);
        userRepository.save(user);

        user = new Users();
        user.setName("two");
        user.setDateOfBirth(LocalDate.now());
        user.setPassword(passwordEncoder.encode("2"));
        userRepository.save(user);
        emailData = new EmailData();
        emailData.setUser(user);
        emailData.setEmail("two@mail.mail");
        emailDataRepository.save(emailData);
        phoneData = new PhoneData();
        phoneData.setUser(user);
        phoneData.setPhone("2");
        phoneDataRepository.save(phoneData);
        recipient = new Account();
        recipient.setUser(user);
        recipient.setStartBalance(BigDecimal.valueOf(200));
        recipient.setBalance(BigDecimal.valueOf(200));
        recipient.setStartDate(new Timestamp(System.currentTimeMillis()));
        accountRepository.save(recipient);

        user.setAccount(recipient);
        userRepository.save(user);
    }

    @Test
    @Transactional
    void transfer_shouldTransferMoneySuccessfully() {
        createUsers();
        transactionService.transfer(sender.getId(), recipient.getId(), amount);
        assertEquals(0, sender.getBalance().compareTo(BigDecimal.valueOf(400)));
        assertEquals(0, recipient.getBalance().compareTo(BigDecimal.valueOf(300)));
    }

    @Test
    @Transactional
    void transfer_shouldThrowExceptionIfSenderNotFound() {
        createUsers();
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.transfer(sender.getId(), 750L, amount)
        );

        assert exception.getMessage().contains(USER_NOT_FOUND + 750L);
    }

    @Test
    @Transactional
    void transfer_shouldThrowExceptionIfRecipientNotFound() {
        createUsers();
        Exception exception = assertThrows(EntityNotFoundException.class, () ->
                transactionService.transfer(750L, recipient.getId(), amount)
        );
        assert exception.getMessage().contains(USER_NOT_FOUND + 750L);
    }
}