package com.example.bookshop.cartservice.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SachDto {
    private int id;
    private String ten;
    private String anh;
    private Boolean trangThai;
    private BigDecimal gia;
    private Integer soLuong;
}
