package com.cardpulse.payload.response;

import com.cardpulse.data.BankInfo;
import com.cardpulse.data.CountryInfo;
import com.cardpulse.data.NumberInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class BinLookupResponse {
    @JsonProperty
    NumberInfo number;
    @JsonProperty
    String scheme;
    @JsonProperty
    String type;
    @JsonProperty
    String brand;
    boolean prepaid;
    @JsonProperty
    CountryInfo country;
    @JsonProperty
    BankInfo bank;


}
