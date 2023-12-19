package com.example.bankingportal.service;

import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.dto.RegisterResponseDto;
import com.example.bankingportal.dto.UpdateUserDto;
import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.entity.VerificationToken;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.repository.UserRepository;
import com.example.bankingportal.repository.VerificationRepository;
import com.example.bankingportal.util.CurrentUser;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final VerificationRepository verificationRepository;

    private final EmailService emailService;

    @Value("${spring.application.name}")
    private String serviceName;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           VerificationRepository verificationRepository,EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.verificationRepository=verificationRepository;
        this.emailService=emailService;

    }

    @Override
    public RegisterResponseDto registerUser(User user) {
       try {

           String encodedPassword = passwordEncoder.encode(user.getPassword());
           user.setPassword(encodedPassword);

           VerificationToken verificationToken=createVerificationToken(user);

           String userId=generateUniqueUserId();
           VerificationToken verificationTokenDb=verificationRepository.save(verificationToken);
           user.setToken(verificationTokenDb);
           user.setUserId(userId);
           userRepository.save(user);
           emailService.sendVerificationEmail(user.getEmail(),verificationToken.getToken());

           return RegisterResponseDto.toRegisterResponseDto(user);
       }catch (Exception e){
           throw new BankingServiceException(serviceName,e.getMessage(),1);
       }
    }

    public VerificationToken createVerificationToken(User user) {
        String verificationToken = UUID.randomUUID().toString();
        return VerificationToken.builder().token(verificationToken).user(user).expiryDate(LocalDateTime.now().plusDays(1)).build();
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        Optional<VerificationToken> optionalVerificationToken=verificationRepository.findByToken(token);
        if(optionalVerificationToken.isEmpty())
            throw new BankingServiceException(serviceName, BankingServiceError.INVALID_TOKEN.getMessage(),BankingServiceError.INVALID_TOKEN.getErrorCode());
        VerificationToken verificationToken=optionalVerificationToken.get();
        if(verificationToken.isExpired()){

            throw new BankingServiceException(serviceName,BankingServiceError.TOKEN_EXPIRED.getMessage(), BankingServiceError.TOKEN_EXPIRED.getErrorCode());
        }

        User user = verificationToken.getUser();
        user.setEmailVerified(true);
        user.setToken(null);
        userRepository.save(user);
        verificationToken.setUser(null);
        verificationRepository.save(verificationToken);
        verificationRepository.delete(verificationToken);

    }

    @Override
    public RegisterResponseDto getUserDetailsByUserId(String userId) {
        Optional<User> optionalUser=userRepository.findByUserId(userId);
        if(optionalUser.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.USER_DO_NOT_EXIST.getMessage(), BankingServiceError.USER_DO_NOT_EXIST.getErrorCode());
        User user=optionalUser.get();
        RegisterResponseDto registerResponseDto = RegisterResponseDto.toRegisterResponseDto(user);
        List<Account> accountList=user.getAccount();
        List<String> strAccList = accountList.stream().map(Account::getAccountNumber).toList();
        registerResponseDto.setAccountNumber(strAccList);
        return registerResponseDto;
    }

    private String generateUniqueUserId() {
        String userId;
        do {
            // Generate a UUID as the account number
            userId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 6);
        } while (userRepository.findByUserId(userId).isPresent());

        return userId;
    }

    @Override
    public User updateUser(UpdateUserDto updateUserDto) {
        Optional<User> optionalUser = userRepository.findByUserId(CurrentUser.getUserId());
        if (optionalUser.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.USER_DO_NOT_EXIST.getMessage(), BankingServiceError.USER_DO_NOT_EXIST.getErrorCode());
        User existingUser=optionalUser.get();
        if(updateUserDto.getName()!=null)
            existingUser.setName(updateUserDto.getName());
        if(updateUserDto.getEmail()!=null)
            existingUser.setEmail(updateUserDto.getEmail());
        if(updateUserDto.getAddress()!=null)
            existingUser.setAddress(updateUserDto.getAddress());
        if(updateUserDto.getPhoneNumber()!=null)
            existingUser.setPhoneNumber(updateUserDto.getPhoneNumber());
        return userRepository.save(existingUser);
    }

    @Override
    public void resendVerificationEmail(String userID){
        Optional<User> optionalUser=userRepository.findByUserId(userID);
        if(optionalUser.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.USER_DO_NOT_EXIST.getMessage(), BankingServiceError.USER_DO_NOT_EXIST.getErrorCode());
        User user=optionalUser.get();

        VerificationToken verificationToken=createVerificationToken(user);

        VerificationToken verificationTokenDb=verificationRepository.save(verificationToken);
        user.setToken(verificationTokenDb);
        userRepository.save(user);
        emailService.sendVerificationEmail(user.getEmail(),verificationToken.getToken());

    }

}
