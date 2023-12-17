package com.example.backEnd.controller;

import com.example.backEnd.dto.OrderDto;
import com.example.backEnd.entity.Cart;
import com.example.backEnd.entity.Item;
import com.example.backEnd.entity.Order;
import com.example.backEnd.repository.CartRepository;
import com.example.backEnd.repository.ItemRepository;
import com.example.backEnd.repository.OrderRepository;
import com.example.backEnd.service.JwtService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class OrderController {

    @Autowired
    JwtService jwtService;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    CartRepository cartRepository;

    /**
     * 주문 조회
     */
    @GetMapping("/api/orders")
    public ResponseEntity<?> getOrder(
            @CookieValue(value = "token", required = false) String token
    ){
        //token 값이 유효 한지 확인
        if(!jwtService.isValid(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        List<Order> orders = orderRepository.findByMemberIdOrderByIdDesc(memberId);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    /**
     * 주문 등록
     */
    @Transactional
    @PostMapping("/api/orders")
    public ResponseEntity<?> pushOrder(
            @RequestBody OrderDto dto,
            @CookieValue(value = "token", required = false) String token
    ){
        //token 값이 유효 한지 확인
        if(!jwtService.isValid(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        int memberId = jwtService.getId(token);
        Order newOrder = new Order();
        newOrder.setMemberId(memberId);
        newOrder.setName(dto.getName());
        newOrder.setAddress(dto.getAddress());
        newOrder.setPayment(dto.getPayment());
        newOrder.setCardNumber(dto.getCardNumber());
        newOrder.setItems(dto.getItems());
        orderRepository.save(newOrder);     //주문 등록 처리

        cartRepository.deleteByMemberId(memberId);      //카트 비우기

        return new ResponseEntity<>(HttpStatus.OK);
    }

}
