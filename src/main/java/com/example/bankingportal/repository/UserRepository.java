package com.example.bankingportal.repository;

import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByAccountAccountNumber(String accountNo);

    Optional<User> findByUserId(String userId);
}
