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

    public CartDto getCart(Long userId, String authorization){
        Cart cart = hashOperations.get(KEY_CART, userId);
        if(cart == null) {
            throw new CartNotFoundException("Không tìm thấy giỏ hàng");
        }
        List<Sach> sachList = cart.getSachList();
        List<SachDto> sachDtos = new java.util.ArrayList<>(sachList.stream().map(CommonMapper::mapToSachDto).toList());
        int sachDtoCount = sachDtos.size();
        Object paramValues;
        if(sachDtoCount > 1) {
            paramValues = sachDtos.stream().map(SachDto::getId).toList();
        } else {
            paramValues = sachDtos.get(0).getId();
        }
//        queryWithParam("http://sach/api/sach/trangThaiGia", "sachIds", paramValues, TrangThaiSach[].class, authorization);
        TrangThaiSach[] trangThaiSaches =
                webClientBuilder.build().get()
                .uri("http://sach/api/sach/trangThaiGia",
                        uriBuilder -> uriBuilder
                                .queryParam("sachIds", sachDtos.stream().map(SachDto::getId).toList())
                                .build()
                ).header("Authorization", authorization)
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

    public Integer getCartAmount(Long accountId, String auth) {
        CartDto cartDto = getCart(accountId, auth);
        return cartDto.getSachList().stream()
                .reduce(0, (integer, sachDto) -> integer + sachDto.getSoLuong(), Integer::sum);
    }

    public void setCart(Long userId, SachDto sachDto, String authorization){
        Boolean isUserAllowed = queryWithParam("http://account/api/account", "id", userId, Boolean.class, authorization);
        if(!isUserAllowed) {
            throw new InvalidBodyException("Tài khoản không thể đặt hàng");
        }
        Cart cart = hashOperations.get(KEY_CART, userId);
        Sach sach = CommonMapper.mapToSach(sachDto);
        if(cart == null) {
            ArrayList<Sach> sachList = new ArrayList<>();
            Long id = queryWithParam("http://sach/api/sach/id", "sachId", sach.getId(), Long.class, authorization);
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
    
    private <K, V> V queryWithParam(String uri, String queryParam, K value, Class<V> valueType, String authorization) {
        return webClientBuilder.build().get()
                .uri(uri,
                        uriBuilder -> uriBuilder
                                .queryParam(queryParam, value)
                                .build())
                .header("Authorization", authorization)
                .retrieve()
                .bodyToMono(valueType)
                .block();
    }
}
