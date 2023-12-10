package com.cardpulse.payload.response;

import com.cardpulse.enums.Gender;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SignInResponse {
    String token;
    Integer id;
    String name;
    String email;
    Gender gender;
    Integer age;
    List<String> roles;
    String username;
}
