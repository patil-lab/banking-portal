package com.example.bankingportal.repository;

import com.example.bankingportal.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {

   Optional<Account> findByAccountNumber(String accountNo);

   List<Account> findByUserUserId(String userId);
}
