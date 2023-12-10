package com.cardpulse.payload.response;

import com.cardpulse.enums.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserDetailResponse {
    Integer id;
    String name;
    String email;
    Integer age;
    Gender gender;
}
