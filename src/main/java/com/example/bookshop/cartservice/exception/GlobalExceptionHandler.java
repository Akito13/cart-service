package com.example.bookshop.cartservice.exception;

import com.example.bookshop.cartservice.dto.CartDto;
import com.example.bookshop.cartservice.dto.ErrorResponseDto;
import com.example.bookshop.cartservice.dto.ResponseDto;
import com.example.bookshop.cartservice.mapper.CommonMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleCartNotFoundException(CartNotFoundException exception, WebRequest request){
        return new ResponseEntity<>(CommonMapper.buildErrorResponse(exception, request, null, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidBodyException.class)
    public ResponseEntity<ErrorResponseDto> handleCartNotFoundException(InvalidBodyException exception, WebRequest request){
        return new ResponseEntity<>(CommonMapper.buildErrorResponse(exception, request, null, HttpStatus.BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseDto<CartDto>> handleGlobalException(Exception exception, WebRequest request){
        ResponseDto<CartDto> responseDto = ResponseDto.<CartDto>builder()
                .apiPath(request.getDescription(false))
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR)
                .payload(null)
                .build();
        exception.printStackTrace();
        return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
