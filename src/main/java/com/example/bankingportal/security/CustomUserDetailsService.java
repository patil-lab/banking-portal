package com.example.bankingportal.security;

import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.repository.AccountRepository;
import com.example.bankingportal.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        Optional<User> optionalUser=userRepository.findByUserId(userId);
        if(optionalUser.isEmpty())
            throw new UsernameNotFoundException("Invalid User");
        User user=optionalUser.get();

        return new org.springframework.security.core.userdetails.User(user.getUserId(),
                user.getPassword(), Collections.emptyList());
    }
}
