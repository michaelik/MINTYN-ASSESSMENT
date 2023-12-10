package com.cardpulse.data;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
public class CountryInfo {
    String numeric;
    String alpha2;
    String name;
    String emoji;
    String currency;
    double latitude;
    double longitude;
}
