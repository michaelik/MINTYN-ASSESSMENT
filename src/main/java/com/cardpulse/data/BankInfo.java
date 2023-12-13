package com.cardpulse.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class BankInfo {
    @JsonProperty
    String name;
}
