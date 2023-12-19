package com.example.bankingportal.service;

import com.example.bankingportal.constants.CurrencyType;
import com.example.bankingportal.dto.RegisterResponseDto;
import com.example.bankingportal.dto.UpdateUserDto;
import com.example.bankingportal.entity.Account;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.entity.VerificationToken;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.repository.UserRepository;
import com.example.bankingportal.repository.VerificationRepository;
import com.example.bankingportal.util.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private VerificationRepository verificationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

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
    public void testRegisterUser() {

        // Mock dependencies
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(verificationRepository.save(any())).thenReturn(new VerificationToken());
        when(userRepository.save(any())).thenReturn(new User());
        doNothing().when(emailService).sendVerificationEmail(anyString(), anyString());

        // Mock user
        User user = new User();
        user.setPassword("password");
        user.setEmail("patil@gmail.com");

        RegisterResponseDto responseDto = userService.registerUser(user);

        // Verify that the necessary methods were called
        verify(passwordEncoder, times(1)).encode("password");
        verify(verificationRepository, times(1)).save(any(VerificationToken.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(eq("patil@gmail.com"), anyString());

        // You can add additional assertions based on your business logic and the expected behavior.
    }

    @Test
    void testVerifyEmail() {

        // Mock dependencies
        User user = new User();
        user.setPassword("password");
        user.setEmail("patil@gmail.com");
        VerificationToken verificationToken=new VerificationToken();
        verificationToken.setExpiryDate(LocalDateTime.now().plusDays(1));
        verificationToken.setUser(user);
        when(verificationRepository.findByToken(anyString())).thenReturn(Optional.of(verificationToken));
        when(userRepository.save(any())).thenReturn(new User());

        // Call the method to be tested
        userService.verifyEmail("validToken");

        // Verify that the necessary methods were called
        verify(verificationRepository, times(1)).findByToken("validToken");
        verify(userRepository, times(1)).save(any(User.class));
        verify(verificationRepository, times(1)).save(any(VerificationToken.class));
        verify(verificationRepository, times(1)).delete(any(VerificationToken.class));
    }

    @Test
    void testGetUserDetailsByUserIdSuccess() {

        Account account1=new Account();
        account1.setAccountNumber("1233");
        account1.setCurrencyType(CurrencyType.INR);
        Account account2=new Account();
        account2.setCurrencyType(CurrencyType.INR);
        account2.setAccountNumber("234");
        User mockedUser = new User();
        mockedUser.setUserId("testUserId");
        mockedUser.setAccount(Arrays.asList(account1, account2));

        when(userRepository.findByUserId("testUserId")).thenReturn(Optional.of(mockedUser));

        // Call the method to be tested
        RegisterResponseDto result = userService.getUserDetailsByUserId("testUserId");
        // Verify the expected behavior
        assertEquals("testUserId", result.getUserId());
        assertEquals(Arrays.asList("1233", "234"), result.getAccountNumber());
    }

    @Test
    void testGetUserDetailsUserNotFound(){

        BankingServiceException exception = assertThrows(BankingServiceException.class, () -> {
            // Code that should throw YourExceptionType
            userService.getUserDetailsByUserId("testUserId");
        });

        // Optional: Assert additional details about the exception
        assertEquals("User does not exist", exception.getError().getMessage());
    }



    @Test
    void testUpdateUserSuccess() {

        // Mock the userRepository to return an existing user
        User existingUser = new User();
        existingUser.setUserId("testUserId");
        existingUser.setName("OldName");
        existingUser.setEmail("old@example.com");
        existingUser.setAddress("OldAddress");
        existingUser.setPhoneNumber("OldPhoneNumber");

        User newUser = new User();
        newUser.setUserId("testUserId");
        newUser.setName("NewName");
        newUser.setEmail("new@example.com");
        newUser.setAddress("NewAddress");
        newUser.setPhoneNumber("NewPhoneNumber");
        when(userRepository.findByUserId("testUserId")).thenReturn(Optional.of(newUser));
        when(userRepository.save(newUser)).thenReturn(newUser); // Mocking save to return the same entity


        // Create an update DTO with new values
        UpdateUserDto updateUserDto = new UpdateUserDto();
        updateUserDto.setName("NewName");
        updateUserDto.setEmail("new@example.com");
        updateUserDto.setAddress("NewAddress");
        updateUserDto.setPhoneNumber("NewPhoneNumber");
        // Call the method to be tested
        User updatedUser = userService.updateUser(updateUserDto);

        // Verify the expected behavior
        assertEquals("testUserId", updatedUser.getUserId());
        assertEquals("NewName", updatedUser.getName());
        assertEquals("new@example.com", updatedUser.getEmail());
        assertEquals("NewAddress", updatedUser.getAddress());
        assertEquals("NewPhoneNumber", updatedUser.getPhoneNumber());
    }

    @Test
    void testUpdateUserUserNotFound(){
        BankingServiceException exception = assertThrows(BankingServiceException.class, () -> {
            UpdateUserDto updateUserDto = new UpdateUserDto();
            updateUserDto.setName("NewName");
            updateUserDto.setEmail("new@example.com");
            updateUserDto.setAddress("NewAddress");
            updateUserDto.setPhoneNumber("NewPhoneNumber");
            userService.updateUser(updateUserDto);
        });

        // Optional: Assert additional details about the exception
        assertEquals("User does not exist", exception.getError().getMessage());
    }

}