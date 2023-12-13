package com.cardpulse.payload.response;

import com.cardpulse.data.BankInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class BinLookupResponse {
    @JsonProperty
    String scheme;
    @JsonProperty
    String type;
    @JsonProperty
    BankInfo bank;
}
