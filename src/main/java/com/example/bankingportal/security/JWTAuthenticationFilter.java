package com.example.bankingportal.security;


import com.example.bankingportal.constants.BankingServiceError;
import com.example.bankingportal.exception.BankingServiceException;
import com.example.bankingportal.service.DashBoardServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;

    private final JWTTokenGenerator jwtTokenGenerator;

    @Value("${spring.application.name}")
    private String serviceName;

    private final Logger LOGGER = LoggerFactory.getLogger(DashBoardServiceImpl.class);


    @Autowired
    public JWTAuthenticationFilter(CustomUserDetailsService userDetailsService, JWTTokenGenerator jwtTokenGenerator) {
        this.customUserDetailsService = userDetailsService;
        this.jwtTokenGenerator = jwtTokenGenerator;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestTokenHeader = request.getHeader("Authorization");

        String userId = null;
        String token = null;

        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
            token = requestTokenHeader.substring(7);
            try {
                userId = this.jwtTokenGenerator.getUsernameFromToken(token);
            } catch (IllegalArgumentException e) {
                LOGGER.error("UnAuthorized:Unable to get token");
            } catch (ExpiredJwtException e) {
                LOGGER.error("UnAuthorized:Token Expired");

            } catch (MalformedJwtException e) {
                LOGGER.error("UnAuthorized:Malformed JWT Token");
            }

        } else {
            LOGGER.error("UnAuthorized:JWT Token does not begin with Bearer String");
        }

        if (userId != null ) {

            UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(userId);
            if(!jwtTokenGenerator.validateToken(token,userDetails)){
                throw new BankingServiceException(serviceName, BankingServiceError.INVALID_JWT_TOKEN.getMessage() ,BankingServiceError.INVALID_JWT_TOKEN.getErrorCode());
            }

            Authentication authentication = jwtTokenGenerator.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }

        filterChain.doFilter(request, response);
    }
}
