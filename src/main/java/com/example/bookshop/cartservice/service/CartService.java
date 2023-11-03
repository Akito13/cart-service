package com.example.bookshop.cartservice.service;

import com.example.bookshop.cartservice.dto.CartDto;
import com.example.bookshop.cartservice.dto.SachDto;
import com.example.bookshop.cartservice.dto.TrangThaiSach;
import com.example.bookshop.cartservice.exception.CartNotFoundException;
import com.example.bookshop.cartservice.exception.InvalidBodyException;
import com.example.bookshop.cartservice.mapper.CommonMapper;
import com.example.bookshop.cartservice.model.Cart;
import com.example.bookshop.cartservice.model.Sach;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class CartService {

    private final String KEY_CART = "CART";
    private final WebClient.Builder webClientBuilder;
    private RedisTemplate<String, Cart> redisTemplate;

    private HashOperations<String, Long, Cart> hashOperations;

    @Autowired
    public CartService(WebClient.Builder webClientBuilder, RedisTemplate<String, Cart> redisTemplate) {
        this.webClientBuilder = webClientBuilder;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    private void init() {
        hashOperations = redisTemplate.opsForHash();
    }

    public CartDto getCart(Long userId){
        Cart cart = hashOperations.get(KEY_CART, userId);
        if(cart == null) {
            throw new CartNotFoundException("Không tìm thấy giỏ hàng");
        }
        List<Sach> sachList = cart.getSachList();
        List<SachDto> sachDtos = new java.util.ArrayList<>(sachList.stream().map(CommonMapper::mapToSachDto).toList());
        TrangThaiSach[] trangThaiSaches = webClientBuilder.build().get()
                .uri("http://sach/api/sach/trangThaiGia",
                        uriBuilder -> uriBuilder
                                .queryParam("sachIds", sachDtos.stream().map(SachDto::getId).toList())
                                .build())
                .retrieve()
                .bodyToMono(TrangThaiSach[].class)
                .block();
        try {
            sachDtos.removeIf(sachDto -> {
                int index = Arrays.binarySearch(trangThaiSaches, sachDto.getId());
                TrangThaiSach tts = trangThaiSaches[index];
                sachDto.setGia(tts.getGia());
                sachDto.setTrangThai(tts.getTrangThai());
                if(!tts.getTrangThai()){
                    sachList.remove(CommonMapper.mapToSach(sachDto));
                    return true;
                }
                return false;
            });
            hashOperations.put(KEY_CART, userId, cart);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Xảy ra lỗi, vui lòng thử lại sau.");
        }
        return new CartDto(cart.getId(), sachDtos);
    }

    public void setCart(Long userId, SachDto sachDto){
        Cart cart = hashOperations.get(KEY_CART, userId);
        Sach sach = CommonMapper.mapToSach(sachDto);
        if(cart == null) {
            ArrayList<Sach> sachList = new ArrayList<>();
            int id = queryWithParam("http://sach/api/sach/id", "sachId", sach.getId(), Integer.class);
            if(id < 0) {
                throw new InvalidBodyException("Sách không tồn tại");
            }
            sachList.add(sach);
            cart = new Cart(userId, sachList);
        } else {
            ArrayList<Sach> sachList = cart.getSachList();
            int index = sachList.indexOf(sach);
            if(index >= 0) {
                Sach foundSach = sachList.get(index);
                int soLuongConLai = foundSach.getSoLuong() + sach.getSoLuong();
                if(soLuongConLai <= 0) {
                    sachList.remove(index);
                    if(sachList.isEmpty()) {
                        hashOperations.delete(KEY_CART, userId);
                        return;
                    }
                } else {
                    foundSach.setSoLuong(soLuongConLai);
                }
            } else {
                if(sachDto.getSoLuong() <= 0) {
                    throw new InvalidBodyException("Số lượng không hợp lệ");
                }
                sachList.add(sach);
            }
        }
        hashOperations.put(KEY_CART, userId, cart);
    }
    
    private <V> V queryWithParam(String uri, String queryParam, V value, Class<V> valueType) {
        return webClientBuilder.build().get()
                .uri(uri,
                        uriBuilder -> uriBuilder
                                .queryParam(queryParam, value)
                                .build())
                .retrieve()
                .bodyToMono(valueType)
                .block();
    }
}
