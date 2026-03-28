package com.akshat.college_project.repository;

import com.akshat.college_project.entity.Otp;
import com.akshat.college_project.entity.enums.AccountType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OtpRepository extends JpaRepository<Otp, UUID> {

    List<Otp> findByEmailAndAccountTypeAndIsUsedFalse(String email, AccountType accountType);

    Optional<Otp> findTopByEmailAndAccountTypeAndCodeAndIsUsedFalseOrderByCreatedAtDesc(
            String email,
            AccountType accountType,
            String code
    );
}
