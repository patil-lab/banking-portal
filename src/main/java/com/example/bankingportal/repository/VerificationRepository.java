package com.example.bankingportal.repository;

import com.example.bankingportal.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationRepository extends JpaRepository<VerificationToken,Long> {

    Optional<VerificationToken> findByToken(String token);

}
