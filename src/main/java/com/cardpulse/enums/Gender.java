package com.cardpulse.enums;

import javax.validation.constraints.Pattern;

public enum Gender {
    M("MALE"),
    F("FEMALE");

    @Pattern(regexp = "[MF]")
    private final String gender;

    Gender(String gender){
        this.gender = gender;
    }
}
