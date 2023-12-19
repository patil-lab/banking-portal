package com.example.bankingportal.config;

import com.example.bankingportal.security.JWTAuthEntryPoint;
import com.example.bankingportal.security.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig{

    private static final String[] WHITE_LIST_URL = {"/api/v1/auth/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};

    private final JWTAuthEntryPoint jwtAuthEntryPoint;

    private final JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    public SecurityConfig(JWTAuthEntryPoint jwtAuthEntryPoint,JWTAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthEntryPoint = jwtAuthEntryPoint;
        this.jwtAuthenticationFilter=jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        http
                .csrf(AbstractHttpConfigurer::disable);
        http
                .cors(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(request -> {
            request.requestMatchers(HttpMethod.GET).permitAll()
                    .requestMatchers(WHITE_LIST_URL).permitAll()
                    .requestMatchers("/api/v1.0/users/register").permitAll()
                    .requestMatchers("/api/v1.0/users/login").permitAll();
            request.requestMatchers("/api/v1.0/**").authenticated();
        });
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.httpBasic(Customizer.withDefaults());
        http.exceptionHandling(auth->auth.authenticationEntryPoint(jwtAuthEntryPoint));
        return http.build();

    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
