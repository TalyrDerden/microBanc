package com.home.microBanc.repository;

import com.home.microBanc.modal.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {
    @Query("SELECT u FROM Users u " +
            "JOIN u.emailData e " +
            "JOIN u.phoneData p " +
            "WHERE e.email = :identifier OR p.phone = :identifier")
    Optional<Users> findByEmailOrPhone(@Param("identifier") String identifier);

    boolean existsByName(String username);

    Optional<Users> findByName(String name);

}