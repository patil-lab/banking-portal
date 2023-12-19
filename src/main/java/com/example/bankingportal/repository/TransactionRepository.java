package com.example.bankingportal.repository;

import com.example.bankingportal.entity.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction,Long> {

    Page<Transaction> findByUserId(String userId,PageRequest pageRequest);

    Optional<Transaction> findTransactionByTransactionId(String transactionId);
}
