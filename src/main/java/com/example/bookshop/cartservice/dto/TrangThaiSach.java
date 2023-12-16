package com.example.bookshop.cartservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TrangThaiSach implements Comparable<Long>{
    private int sachId;
    private BigDecimal gia;
    private Boolean trangThai;
    private Double phanTramGiam;
    private Integer soLuong;

    @Override
    public boolean equals(Object obj) {
        TrangThaiSach tts = (TrangThaiSach) obj;
        return tts.getSachId() == this.sachId;
    }

    @Override
    public int compareTo(Long o) {
        return (int)(this.sachId - o);
    }
}
