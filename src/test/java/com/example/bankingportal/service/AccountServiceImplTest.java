package com.example.bankingportal.service;

import com.example.bankingportal.config.KafkaProducerConfig;
import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.AccountResponseDto;
import com.example.bankingportal.dto.TransactionDto;
import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.repository.AccountRepository;
import com.example.bankingportal.repository.TransactionRepository;
import com.example.bankingportal.repository.UserRepository;
import com.example.bankingportal.util.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AccountServiceImpl accountService;

    @Mock
    private KafkaProducerConfig kafkaProducerConfig;

    @Mock
    private TransactionRepository transactionRepository;


    private static MockedStatic<CurrentUser> currentUserMockedStatic;

    @BeforeEach
    public void setUp() {
        currentUserMockedStatic = Mockito.mockStatic(CurrentUser.class, Mockito.RETURNS_DEEP_STUBS);
        when(CurrentUser.getUserId()).thenReturn("testUserId");
    }

    @AfterEach
    public void tearDown() {
        currentUserMockedStatic.close();
    }

    @Test
    void testCreateAccount() {
        Account account1 = new Account();
        account1.setAccountNumber("1233");
        account1.setCurrencyType(CurrencyType.INR);
        Account account2 = new Account();
        account2.setCurrencyType(CurrencyType.INR);
        account2.setAccountNumber("234");
        User mockedUser = new User();
        mockedUser.setUserId("testUserId");
        mockedUser.setAccount(Arrays.asList(account1, account2));

        when(userRepository.findByUserId("testUserId")).thenReturn(Optional.of(mockedUser));
        when(accountRepository.save(ArgumentMatchers.any())).thenReturn(new Account());

        Account account = accountService.createAccount("testUserId", CurrencyType.USD);
        assertNotNull(account);
        assertEquals("ACTIVE", account.getAccount_status().name());
        verify(accountRepository, times(1)).save(any(Account.class));
        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void testCreateAccountUserNotFound() {
        when(userRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        BankingServiceException exception = assertThrows(BankingServiceException.class,
                () -> accountService.createAccount("nonExistentUserId", CurrencyType.USD));
        assertEquals("User does not exist", exception.getError().getMessage());
        verify(accountRepository, never()).save(new Account());
        verify(userRepository, never()).save(new User());

    }

    @Test
    void testIsPinCreated() {

        Account account = new Account();
        account.setPin("2345");
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));

        assertTrue(accountService.isPinCreated("existingAccountNumber"));

        when(accountRepository.findByAccountNumber("nonexistentAccountNumber")).thenReturn(Optional.empty());
        BankingServiceException exception = assertThrows(BankingServiceException.class, () -> {
            accountService.isPinCreated("nonexistentAccountNumber");
        });
        assertEquals(BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), exception.getError().getMessage());
        assertEquals(BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode(), exception.getError().getCode());

        verify(accountRepository, times(1)).findByAccountNumber("existingAccountNumber");
        verify(accountRepository, times(1)).findByAccountNumber("nonexistentAccountNumber");

    }

    @Test
    void testCreatePIN() {
        Account account = new Account();
        account.setPin("2345");
        User user = new User();
        user.setPassword("password");
        user.setEmail("patil@gmail.com");
        account.setUser(user);
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // Case 1: PIN created successfully
        accountService.createPIN("existingAccountNumber", "validPassword", "newPIN");

        verify(accountRepository, times(1)).findByAccountNumber("existingAccountNumber");
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(accountRepository, times(1)).save(any());

        // Case 2: Account not found
        when(accountRepository.findByAccountNumber("nonexistentAccountNumber")).thenReturn(Optional.empty());
        BankingServiceException exception = assertThrows(BankingServiceException.class, () -> {
            accountService.createPIN("nonexistentAccountNumber", "password", "newPIN");
        });
        assertEquals(BankingServiceError.ACCOUNT_NOT_FOUND.getMessage(), exception.getError().getMessage());
        assertEquals(BankingServiceError.ACCOUNT_NOT_FOUND.getErrorCode(), exception.getError().getCode());

        // Case 3: Incorrect password
        when(accountRepository.findByAccountNumber(anyString())).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        exception = assertThrows(BankingServiceException.class, () -> {
            accountService.createPIN("existingAccountNumber", "incorrectPassword", "newPIN");
        });
        assertEquals(BankingServiceError.PASSWORD_DOES_NOT_MATCH.getMessage(), exception.getError().getMessage());
        assertEquals(BankingServiceError.PASSWORD_DOES_NOT_MATCH.getErrorCode(), exception.getError().getCode());

        verify(accountRepository, times(3)).findByAccountNumber(anyString());
        verify(passwordEncoder, times(2)).matches(anyString(), anyString());
        verify(accountRepository, times(1)).save(any());

    }

    @Test
    void testUpdatePIN() {
        String accountNumber = "yourAccountNumber";
        String oldPIN = "oldPIN";
        String password = "userPassword";
        String newPIN = "newPIN";

        Account account = new Account();
        User user = new User();
        user.setPassword("password");
        user.setEmail("patil@gmail.com");
        account.setUser(user);
        account.setPin(passwordEncoder.encode(oldPIN));

        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);

        assertDoesNotThrow(() -> accountService.updatePIN(accountNumber, oldPIN, password, newPIN));

        verify(accountRepository, times(1)).save(any());
        assertEquals(passwordEncoder.encode(newPIN), account.getPin());

    }


    @Test
    void testCashDeposit() {

        String sourceAccountNumber = "123456789";
        String sourcePin = "1234";
        BigDecimal depositAmount = new BigDecimal("100.00");

        User user = new User();
        user.setEmailVerified(true);

        Account sourceAccount = new Account();
        sourceAccount.setUser(user);
        sourceAccount.setAccountNumber(sourceAccountNumber);
        sourceAccount.setBalance(new BigDecimal("500.00"));
        sourceAccount.setPin(sourcePin);

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setSourceAccountNumber(sourceAccountNumber);
        transactionDto.setSourcePin(sourcePin);
        transactionDto.setAmount(depositAmount);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        doNothing().when(kafkaProducerConfig).sendMessage(any());
        when(accountRepository.save(ArgumentMatchers.any())).thenReturn(new Account());

        accountService.cashDeposit(transactionDto);

        // Assert
        verify(accountRepository, times(1)).save(any());
        verify(kafkaProducerConfig, times(1)).sendMessage(any());

    }

    @Test
    void testCashWithdrawal() {

        String sourceAccountNumber = "123456789";
        String sourcePin = "1234";
        BigDecimal depositAmount = new BigDecimal("100.00");

        User user = new User();
        user.setEmailVerified(true);

        Account sourceAccount = new Account();
        sourceAccount.setUser(user);
        sourceAccount.setAccountNumber(sourceAccountNumber);
        sourceAccount.setBalance(new BigDecimal("500.00"));
        sourceAccount.setPin(sourcePin);

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setSourceAccountNumber(sourceAccountNumber);
        transactionDto.setSourcePin(sourcePin);
        transactionDto.setAmount(depositAmount);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        doNothing().when(kafkaProducerConfig).sendMessage(any());
        when(accountRepository.save(ArgumentMatchers.any())).thenReturn(new Account());
        accountService.cashWithdrawal(transactionDto);

        // Assert
        verify(accountRepository, times(1)).save(any());
        verify(kafkaProducerConfig, times(1)).sendMessage(any());


    }


    @Test
    void testFundTransfer() {

        String sourceAccountNumber = "123456789";
        String sourcePin = "1234";
        BigDecimal depositAmount = new BigDecimal("100.00");

        User user = new User();
        user.setEmailVerified(true);

        Account sourceAccount = new Account();
        sourceAccount.setUser(user);
        sourceAccount.setAccountNumber(sourceAccountNumber);
        sourceAccount.setBalance(new BigDecimal("500.00"));
        sourceAccount.setPin(sourcePin);
        sourceAccount.setCurrencyType(CurrencyType.INR);

        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setSourceAccountNumber(sourceAccountNumber);
        transactionDto.setTargetAccountNumber(sourceAccountNumber);
        transactionDto.setSourcePin(sourcePin);
        transactionDto.setAmount(depositAmount);

        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(accountRepository.findByAccountNumber(sourceAccountNumber)).thenReturn(Optional.of(sourceAccount));
        doNothing().when(kafkaProducerConfig).sendMessage(any());
        when(accountRepository.save(ArgumentMatchers.any())).thenReturn(new Account());

        accountService.fundTransfer(transactionDto);

        // Assert
        verify(accountRepository, times(2)).save(any());
        verify(kafkaProducerConfig, times(2)).sendMessage(any());


    }

    @Test
    void testGetAccountDetailsByUser() {
        String userId = "testUserId";
        Account account = new Account();
        account.setPin("2345");
        User user = new User();
        user.setPassword("password");
        user.setEmail("patil@gmail.com");
        user.setUserId("123");
        account.setUser(user);
        Account accoun1 = new Account();
        accoun1.setPin("2345");
        User user1 = new User();
        user1.setPassword("password");
        user1.setEmail("patil@gmail.com");
        user1.setUserId("2345");
        accoun1.setUser(user1);
        List<Account> mockAccountList = Arrays.asList(account, accoun1);

        when(accountRepository.findByUserUserId(userId)).thenReturn(mockAccountList);

        // Act
        AccountResponseDto result = accountService.getAccountDetailsByUser(userId);

        verify(accountRepository, times(1)).findByUserUserId(userId);

    }
}