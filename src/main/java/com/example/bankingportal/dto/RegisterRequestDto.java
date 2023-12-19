package com.example.bankingportal.dto;

import com.example.bankingportal.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    private String password;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;
    private String address;

    @NotBlank(message = "Phone number cannot be blank")
    private String phoneNumber;

    public User toUserEntity(){
        User user =new User();
        user.setAddress(this.address);
        user.setPassword(this.password);
        user.setEmail(this.email);
        user.setPhoneNumber(this.phoneNumber);
        user.setName(this.name);
        user.setEmailVerified(false);
        return user;
    }
}
