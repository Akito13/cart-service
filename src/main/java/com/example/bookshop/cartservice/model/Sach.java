package com.example.bookshop.cartservice.model;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Sach implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private int id;
    private String ten;
    private String anh;
    private Integer soLuong;

    @Override
    public boolean equals(Object obj) {
        Sach other = (Sach) obj;
        return other.getId() == this.id;
    }
}