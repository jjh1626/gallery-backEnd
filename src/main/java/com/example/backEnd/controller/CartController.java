package com.example.backEnd.controller;

import com.example.backEnd.entity.Cart;
import com.example.backEnd.entity.Item;
import com.example.backEnd.repository.CartRepository;
import com.example.backEnd.repository.ItemRepository;
import com.example.backEnd.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class CartController {

    @Autowired
    JwtService jwtService;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    /**
     * 카트에서 아이템 리스트 가져오기
     */
    @GetMapping("/api/cart/items")
    public ResponseEntity<?> getCartItems(@CookieValue(value = "token", required = false) String token){
        //token 값이 유효 한지 확인
        if(!jwtService.isValid(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        //token 값에서 memberId 를 추출
        int memberId = jwtService.getId(token);
        //memberId 로 cart 리스트를 가져와서 거기서 아이템 id 값을 추출하고 그 값을 이용해서 item 리스트를 추출
        List<Cart> carts = cartRepository.findByMemberId(memberId);
        List<Integer> ids = carts.stream().map(Cart::getItemId).toList();
        List<Item> items = itemRepository.findByIdIn(ids);

        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    /**
     * 카트에 아이템 추가
     */
    @PostMapping("/api/cart/items/{itemId}")
    public ResponseEntity<?> pushCartItem(
            @PathVariable("itemId") int itemId,
            @CookieValue(value = "token", required = false) String token
    ){
        //token 값이 유효 한지 확인
        if(!jwtService.isValid(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        //token 값에서 memberId 를 추출 하여 cart 에 해당 item 이 존재 하는지 확인
        int memberId = jwtService.getId(token);
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        //cart 에 존재 하지 않을 경우 새로 등록
        if(cart == null){
            Cart nCart = new Cart();
            nCart.setMemberId(memberId);
            nCart.setItemId(itemId);
            cartRepository.save(nCart);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * 카트에 아이템 삭제
     */
    @DeleteMapping("/api/cart/items/{itemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable("itemId") int itemId,
            @CookieValue(value = "token", required = false) String token
    ){
        //token 값이 유효 한지 확인
        if(!jwtService.isValid(token)){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        //token 값에서 memberId 를 추출 하여 cart 에 해당 item 이 존재 하는지 확인
        int memberId = jwtService.getId(token);
        Cart cart = cartRepository.findByMemberIdAndItemId(memberId, itemId);

        if(cart != null){
            cartRepository.delete(cart);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
