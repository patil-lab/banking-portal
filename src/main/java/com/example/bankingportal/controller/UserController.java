package com.example.bankingportal.controller;

import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.dto.*;
import com.example.bankingportal.entity.User;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.security.CustomUserDetailsService;
import com.example.bankingportal.security.JWTTokenGenerator;
import com.example.bankingportal.service.EmailService;
import com.example.bankingportal.service.UserService;
import com.example.bankingportal.util.CurrentUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "${api-version}/users", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    private final AuthenticationManager authenticationManager;
    private final JWTTokenGenerator jwtTokenUtil;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;

    private final EmailService emailService;

    @Value("${spring.application.name}")
    private String serviceName;

    @Autowired
    public UserController(AuthenticationManager authenticationManager, JWTTokenGenerator jwtTokenUtil, CustomUserDetailsService userDetailsService,
                          UserService userService,EmailService emailService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
        this.emailService=emailService;
    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BankingServiceResult<RegisterResponseDto>> register(@RequestBody @Valid RegisterRequestDto registerRequestDto) {

        RegisterResponseDto registerResponseDto = userService.registerUser(registerRequestDto.toUserEntity());

        System.out.println("hi");
        return ResponseEntity.ok(BankingServiceResult.succeed(registerResponseDto));
    }

    @PostMapping("/login")
    public ResponseEntity<BankingServiceResult<?>> login(@RequestBody @Valid LoginDto loginDto) {
            try{
                        authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDto.getUserId(), loginDto.getAccPassword())
                );
            }catch (Exception e){
                throw new BankingServiceException(serviceName, BankingServiceError.USER_DO_NOT_EXIST.getMessage(),BankingServiceError.USER_DO_NOT_EXIST.getErrorCode());
            }
        // If authentication successful, generate JWT token
        UserDetails userDetails = userDetailsService.loadUserByUsername(loginDto.getUserId());
        System.out.println(userDetails);
        String token = jwtTokenUtil.generateToken(userDetails);
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        // Return the JWT token in the response
        return ResponseEntity.ok(BankingServiceResult.succeed(result));
    }

    @GetMapping("/verify-email")
    public ResponseEntity<BankingServiceResult<String>> verifyEmail(@RequestParam("token") String token) {

            userService.verifyEmail(token);

        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }

    @GetMapping("/reverify-email")
    public ResponseEntity<BankingServiceResult<String>> reVerifyEmail(){

        userService.resendVerificationEmail(CurrentUser.getUserId());
        return ResponseEntity.ok(BankingServiceResult.succeed(HttpStatus.OK.toString()));
    }

    @PatchMapping
    public ResponseEntity<BankingServiceResult<RegisterResponseDto>> updateUser(@RequestBody UpdateUserDto updateUserDto) {
        User updateUser = userService.updateUser(updateUserDto);

        RegisterResponseDto responseDto = new RegisterResponseDto();
        responseDto.setName(updateUser.getName());
        responseDto.setEmail(updateUser.getEmail());


        return ResponseEntity.ok(BankingServiceResult.succeed(responseDto));
    }

    @GetMapping
    public ResponseEntity<BankingServiceResult<RegisterResponseDto>> getUserDetails(){
        return ResponseEntity.ok(BankingServiceResult.succeed(userService.getUserDetailsByUserId(CurrentUser.getUserId())));
    }


}
