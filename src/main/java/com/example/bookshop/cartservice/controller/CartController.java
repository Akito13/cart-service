package com.example.bookshop.cartservice.controller;

import com.example.bookshop.cartservice.dto.CartDto;
import com.example.bookshop.cartservice.dto.ResponseDto;
import com.example.bookshop.cartservice.dto.ResponsePayload;
import com.example.bookshop.cartservice.dto.SachDto;
import com.example.bookshop.cartservice.service.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "api/cart", produces = {MediaType.APPLICATION_JSON_VALUE})
@AllArgsConstructor
public class CartController {

    private CartService cartService;

    @GetMapping("{userId}")
    public ResponseEntity<ResponseDto<CartDto>> getCart(@PathVariable Long userId,
                                                        WebRequest request) {
        CartDto cartDto = cartService.getCart(userId);
        ResponsePayload<CartDto> payload = new ResponsePayload<>(cartDto);
        ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                .apiPath(request.getDescription(false))
                .statusCode(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("OK")
                .payload(payload)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("{userId}")
    public ResponseEntity<ResponseDto<CartDto>> setCart(@PathVariable Long userId,
                                                        @RequestBody SachDto sachDto,
                                                        WebRequest request){
        cartService.setCart(userId, sachDto);
        ResponseDto<CartDto> response = ResponseDto.<CartDto>builder()
                .apiPath(request.getDescription(false))
                .statusCode(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("OK")
                .payload(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
