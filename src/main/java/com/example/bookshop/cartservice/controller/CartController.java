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
@RequestMapping(value = "api/cart", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE})
@AllArgsConstructor
public class CartController {

    private CartService cartService;

    @GetMapping("{userId}")
    public ResponseEntity<ResponseDto<CartDto>> getCart(@PathVariable Long userId,
                                                        @RequestHeader("Authorization") String authorization,
                                                        WebRequest request) {
        CartDto cartDto = cartService.getCart(userId, authorization);
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

    @GetMapping("{userId}/amount")
    public ResponseEntity<Integer> getCartAmount(@PathVariable Long userId,
                                 @RequestHeader("Authorization") String auth) {

        return new ResponseEntity<>(cartService.getCartAmount(userId, auth), HttpStatus.OK);
    }

    @PostMapping("{userId}")
    public ResponseEntity setCart(@PathVariable Long userId,
                                  @RequestBody SachDto sachDto,
                                  @RequestHeader("Authorization") String authorization,
                                  WebRequest request){
        cartService.setCart(userId, sachDto, authorization);
        ResponseDto response = ResponseDto.<CartDto>builder()
                .apiPath(request.getDescription(false))
                .statusCode(HttpStatus.OK)
                .timestamp(LocalDateTime.now())
                .message("OK")
                .payload(null)
                .build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("{userId}")
    public ResponseEntity removeCart(@PathVariable Long userId,
                                     @RequestHeader("Authorization") String authorization){
        cartService.removeCart(userId, authorization);
        return ResponseEntity.ok().build();
    }
}
