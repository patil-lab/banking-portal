package com.example.bankingportal.service;

import com.example.bankingportal.config.KafkaProducerConfig;
import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.constants.TransactionType;
import com.example.bankingportal.dto.AccountDto;
import com.example.bankingportal.dto.AccountResponseDto;
import com.example.bankingportal.dto.TransactionDto;
import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.AccountStatus;
import com.example.bankingportal.entity.Transaction;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.repository.AccountRepository;
import com.example.bankingportal.repository.TransactionRepository;
import com.example.bankingportal.repository.UserRepository;
import com.example.bankingportal.util.CurrentUser;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService{

    private final AccountRepository accountRepository;

    private final TransactionRepository transactionRepository;

    private final UserRepository userRepository;

    private final KafkaProducerConfig kafkaProducerConfig;

    private final PasswordEncoder passwordEncoder;

    @Value("${spring.application.name}")
    private String serviceName;

    private final Logger LOGGER = LoggerFactory.getLogger(DashBoardServiceImpl.class);

    @Autowired
    public AccountServiceImpl(AccountRepository accountRepository, TransactionRepository transactionRepository, PasswordEncoder passwordEncoder,
                              UserRepository userRepository,KafkaProducerConfig kafkaProducerConfig) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
        this.userRepository=userRepository;
        this.kafkaProducerConfig=kafkaProducerConfig;
    }

    @Override
    public Account createAccount(String  userId,CurrencyType currencyType) {
        LOGGER.info("create account for userId:"+userId);
        Optional<User> optionalUser=userRepository.findByUserId(userId);
        if(optionalUser.isEmpty())
            throw new BankingServiceException(serviceName, BankingServiceError.USER_DO_NOT_EXIST.getMessage(), BankingServiceError.USER_DO_NOT_EXIST.getErrorCode());
        String accountNumber = generateUniqueAccountNumber();
        Account account = new Account();
        User user = optionalUser.get();
        account.setUser(user);
        account.setAccountNumber(accountNumber);
        account.setBalance(new BigDecimal(BigInteger.ZERO));
        account.setCurrencyType(currencyType);
        account.setAccount_status(AccountStatus.ACTIVE);
        accountRepository.save(account);
        userRepository.save(user);

        return account;
    }

    @Override
    public boolean isPinCreated(String accountNumber) {
        LOGGER.info("Is Pin created? for accountNumber:"+accountNumber);
        Optional<Account> optionalAccount=accountRepository.findByAccountNumber(accountNumber);
        if(optionalAccount.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());

        return optionalAccount.get().getPin()!=null;
    }

    @Override
    public void createPIN(String accountNumber, String password, String pin) {
        LOGGER.info("Create PIN for accountNumber :"+accountNumber);
        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isEmpty()) {
            throw new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());
        }
        Account account=optionalAccount.get();

        if (!passwordEncoder.matches(password, account.getUser().getPassword())) {
         throw new BankingServiceException(serviceName,BankingServiceError.PASSWORD_DOES_NOT_MATCH.getMessage(), BankingServiceError.PASSWORD_DOES_NOT_MATCH.getErrorCode());
        }

        account.setPin(passwordEncoder.encode(pin));
        accountRepository.save(account);

    }

    @Override
    public void updatePIN(String accountNumber, String oldPIN, String password, String newPIN) {
        LOGGER.info("Update PIN for accountNumber:"+accountNumber);
        Optional<Account> optionalAccount=accountRepository.findByAccountNumber(accountNumber);
        if(optionalAccount.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());
        Account account=optionalAccount.get();
        if(!passwordEncoder.matches(oldPIN,account.getPin()))
            throw new BankingServiceException(serviceName,BankingServiceError.PIN_DOES_NOT_MATCH.getMessage(), BankingServiceError.PIN_DOES_NOT_MATCH.getErrorCode());
        if(!passwordEncoder.matches(password,account.getUser().getPassword()))
            throw new BankingServiceException(serviceName,BankingServiceError.PASSWORD_DOES_NOT_MATCH.getMessage(), BankingServiceError.PASSWORD_DOES_NOT_MATCH.getErrorCode());

        account.setPin(passwordEncoder.encode(newPIN));
        accountRepository.save(account);
    }

    @Override
    @Transactional
    public void cashDeposit(TransactionDto transactionDto) {

        LOGGER.info("Cash Deposit for accountNumber :"+transactionDto.getSourceAccountNumber());
        Optional<Account> sourceAccountOptional = accountRepository.findByAccountNumber(transactionDto.getSourceAccountNumber());
        if(sourceAccountOptional.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());
        Account sourceAccount=sourceAccountOptional.get();

        User user=sourceAccount.getUser();
        if(!user.isEmailVerified())
            throw new BankingServiceException(serviceName,BankingServiceError.REVERIFY_EMAIL.getMessage(),BankingServiceError.REVERIFY_EMAIL.getErrorCode());

        if (!passwordEncoder.matches(transactionDto.getSourcePin(), sourceAccount.getPin())) {
            throw new BankingServiceException(serviceName,BankingServiceError.PIN_DOES_NOT_MATCH.getMessage(), BankingServiceError.PIN_DOES_NOT_MATCH.getErrorCode());
        }

        BigDecimal currentBalance = sourceAccount.getBalance();
        BigDecimal depositAmt = transactionDto.getAmount();
        BigDecimal newBalance =currentBalance.add(depositAmt);
        sourceAccount.setBalance(newBalance);
        accountRepository.save(sourceAccount);

        String transactionNum=generateUniqueTransactionNumber();
        Transaction transaction = new Transaction();
        transaction.setAmount(depositAmt);
        transaction.setTransactionId(transactionNum);
        transaction.setTransactionType(TransactionType.CASH_DEPOSIT);
        transaction.setTransactionCurrency(sourceAccount.getCurrencyType());
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setUserId(CurrentUser.getUserId());
        kafkaProducerConfig.sendMessage(transaction);
        LOGGER.info("Transaction sent to Kafka :"+transaction.getTransactionId());

    }

    @Override
    @Transactional
    public void cashWithdrawal(TransactionDto transactionDto) {
        LOGGER.info("Cash Withdrawal for accountNumber :"+transactionDto.getSourceAccountNumber());
        Optional<Account> sourceAccountOptional = accountRepository.findByAccountNumber(transactionDto.getSourceAccountNumber());
        if(sourceAccountOptional.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());
        Account sourceAccount=sourceAccountOptional.get();

        User user=sourceAccount.getUser();
        if(!user.isEmailVerified())
            throw new BankingServiceException(serviceName,BankingServiceError.REVERIFY_EMAIL.getMessage(),BankingServiceError.REVERIFY_EMAIL.getErrorCode());

        if (!passwordEncoder.matches(transactionDto.getSourcePin(), sourceAccount.getPin())) {
            throw new BankingServiceException(serviceName,BankingServiceError.PIN_DOES_NOT_MATCH.getMessage(), BankingServiceError.PIN_DOES_NOT_MATCH.getErrorCode());
        }

        BigDecimal currentBalance = sourceAccount.getBalance();
        BigDecimal withdrawAmt = transactionDto.getAmount();
        if (currentBalance.compareTo(withdrawAmt)<0) {
            throw new BankingServiceException(serviceName,BankingServiceError.INSUFFICIENT_BALANCE.getMessage(), BankingServiceError.INSUFFICIENT_BALANCE.getErrorCode());
        }
        BigDecimal newBalance =currentBalance.subtract(withdrawAmt);
        sourceAccount.setBalance(newBalance);
        accountRepository.save(sourceAccount);

        String transactionNum=generateUniqueTransactionNumber();
        Transaction transaction = new Transaction();
        transaction.setAmount(withdrawAmt);
        transaction.setTransactionId(transactionNum);
        transaction.setTransactionCurrency(sourceAccount.getCurrencyType());
        transaction.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transaction.setSourceAccount(sourceAccount.getAccountNumber());
        transaction.setUserId(CurrentUser.getUserId());
        kafkaProducerConfig.sendMessage(transaction);
        LOGGER.info("Transaction sent to Kafka :"+transaction.getTransactionId());
    }

    @Override
    @Transactional
    public void fundTransfer(TransactionDto transactionDto) {
        LOGGER.info(String.format("Fund Transfer from %s to %s",transactionDto.getSourceAccountNumber(),transactionDto.getTargetAccountNumber()));
        if(transactionDto.getTargetAccountNumber()==null){
            throw new BankingServiceException(serviceName,BankingServiceError.PLEASE_INPUT_TARGET_ACCOUNT.getMessage(),BankingServiceError.PLEASE_INPUT_TARGET_ACCOUNT.getErrorCode());
        }
        Optional<Account> optionalAccount=accountRepository.findByAccountNumber(transactionDto.getSourceAccountNumber());
        if(optionalAccount.isEmpty())
            throw new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(),BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());
        Account sourceAccount=optionalAccount.get();
        User user=sourceAccount.getUser();
        if(!user.isEmailVerified())
            throw new BankingServiceException(serviceName,BankingServiceError.REVERIFY_EMAIL.getMessage(),BankingServiceError.REVERIFY_EMAIL.getErrorCode());

        if (!passwordEncoder.matches(transactionDto.getSourcePin(), sourceAccount.getPin())) {
            throw new BankingServiceException(serviceName,BankingServiceError.PIN_DOES_NOT_MATCH.getMessage(), BankingServiceError.PIN_DOES_NOT_MATCH.getErrorCode());
        }
        Optional<Account> targetOptionalAcc=accountRepository.findByAccountNumber(transactionDto.getTargetAccountNumber());
        if(targetOptionalAcc.isEmpty())
            throw  new BankingServiceException(serviceName,BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode());


        Account targetAccount=targetOptionalAcc.get();
        if(!sourceAccount.getCurrencyType().equals(targetAccount.getCurrencyType())){
            throw new BankingServiceException(serviceName,BankingServiceError.CURRENCY_MISMATCH.getMessage(),BankingServiceError.CURRENCY_MISMATCH.getErrorCode());
        }

        User targetUser=targetAccount.getUser();
        BigDecimal sourceBalance = sourceAccount.getBalance();
        BigDecimal transferAmount = transactionDto.getAmount();
        if (sourceBalance.compareTo(transferAmount)<0) {
            throw new BankingServiceException(serviceName,BankingServiceError.INSUFFICIENT_BALANCE.getMessage(),BankingServiceError.INSUFFICIENT_BALANCE.getErrorCode());
        }

        BigDecimal newSourceBalance =sourceBalance.subtract(transferAmount);
        sourceAccount.setBalance(newSourceBalance);
        accountRepository.save(sourceAccount);

        BigDecimal targetBalance = targetAccount.getBalance();
        BigDecimal newTargetBalance = targetBalance.add(transferAmount);
        targetAccount.setBalance(newTargetBalance);
        accountRepository.save(targetAccount);

        String withdrawTransNum=generateUniqueTransactionNumber();
        Transaction transactionWithdrawal = new Transaction();
        transactionWithdrawal.setAmount(transferAmount);
        transactionWithdrawal.setTransactionType(TransactionType.CASH_WITHDRAWAL);
        transactionWithdrawal.setSourceAccount(sourceAccount.getAccountNumber());
        transactionWithdrawal.setUserId(CurrentUser.getUserId());
        transactionWithdrawal.setTransactionId(withdrawTransNum);
        transactionWithdrawal.setTransactionCurrency(sourceAccount.getCurrencyType());
        kafkaProducerConfig.sendMessage(transactionWithdrawal);
        LOGGER.info("Transaction sent to Kafka :"+transactionWithdrawal.getTransactionId());
        String depositTransNum=generateUniqueTransactionNumber();
        Transaction transactionDeposit= new Transaction();
        transactionDeposit.setAmount(transferAmount);
        transactionDeposit.setTransactionId(depositTransNum);
        transactionDeposit.setTransactionType(TransactionType.CASH_DEPOSIT);
        transactionDeposit.setTransactionCurrency(sourceAccount.getCurrencyType());
        transactionDeposit.setSourceAccount(sourceAccount.getAccountNumber());
        transactionDeposit.setUserId(targetUser.getUserId());
        kafkaProducerConfig.sendMessage(transactionDeposit);
        LOGGER.info("Transaction sent to Kafka :"+transactionDeposit.getTransactionId());

    }

    @Override
    public AccountResponseDto getAccountDetailsByUser(String userid) {
       List<Account> accountList=accountRepository.findByUserUserId(userid);
       List<AccountDto> accountDtoList=accountList.stream().map(AccountDto::toAccountDto).toList();

       return AccountResponseDto.builder().accountDtoList(accountDtoList).build();

    }

    private String generateUniqueTransactionNumber() {
        String transactionNo;
        do {
            // Generate a UUID as the account number
            transactionNo = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        } while (transactionRepository.findTransactionByTransactionId(transactionNo).isPresent());

        return transactionNo;
    }

    private String generateUniqueAccountNumber() {
        String accountNumber;
        do {
            // Generate a UUID as the account number
            accountNumber = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 12);
        } while (accountRepository.findByAccountNumber(accountNumber).isPresent());

        return accountNumber;
    }
}
