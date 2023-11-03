package com.example.bookshop.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponseDto {
    private HttpStatus statusCode;
    private String message;
    private LocalDateTime timestamp;
    private String apiPath;
    private Map<String, String> errors;
}
