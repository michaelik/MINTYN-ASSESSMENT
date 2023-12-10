package com.cardpulse.payload.request;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PUBLIC)
@ToString
public class SignUpRequest {
    @NotBlank(message = "name should not be empty")
    String name;
    @Email
    @NotBlank(message = "email should not be empty")
    String email;
    @NotBlank(message = "password should not be empty")
    String password;
    @Min(value = 20, message = "The age must be 18 or greater")
    @NotNull(message = "age should not be empty")
    Integer age;
    @NotBlank(message = "gender should not be empty")
    @Pattern(regexp = "[MF]", message = "Only currency M and F is accepted")
    String gender;
}

