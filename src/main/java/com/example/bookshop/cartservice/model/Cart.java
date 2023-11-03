package com.example.bookshop.cartservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@RedisHash(timeToLive = 60L)
public class Cart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private ArrayList<Sach> sachList;

}
