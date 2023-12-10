package com.cardpulse.payload.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PayloadResponse {
    String scheme;
    String type;
    String bank;
}
