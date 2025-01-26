package com.home.microBanc.repository;

import com.home.microBanc.modal.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailDataRepository extends JpaRepository<EmailData, Long> {
    boolean existsByEmail(String email);
}
