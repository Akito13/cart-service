package com.example.bookshop.cartservice.mapper;

import com.example.bookshop.cartservice.dto.ErrorResponseDto;
import com.example.bookshop.cartservice.dto.SachDto;
import com.example.bookshop.cartservice.model.Sach;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.Map;

public class CommonMapper {
    public static SachDto mapToSachDto(Sach sach) {
        return SachDto.builder()
                .id(sach.getId())
                .anh(sach.getAnh())
                .ten(sach.getTen())
                .soLuong(sach.getSoLuong())
                .build();
    }

    public static Sach mapToSach(SachDto sachDto) {
        return Sach.builder()
                .id(sachDto.getId())
                .anh(sachDto.getAnh())
                .ten(sachDto.getTen())
                .soLuong(sachDto.getSoLuong())
                .build();
    }

    public static ErrorResponseDto buildErrorResponse(RuntimeException exception, WebRequest request, Map<String, String> errors, HttpStatus httpStatus){
        return ErrorResponseDto.builder()
                .apiPath(request.getDescription(false))
                .message(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .statusCode(httpStatus)
                .errors(errors)
                .build();
    }
}
