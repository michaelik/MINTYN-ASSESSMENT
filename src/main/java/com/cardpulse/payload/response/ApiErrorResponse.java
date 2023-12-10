package com.cardpulse.payload.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiErrorResponse {
    String path;
    List<String> messages;
    int statusCode;
    LocalDateTime createdAt;
}
