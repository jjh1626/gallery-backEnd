package com.example.backEnd.controller;

import com.example.backEnd.entity.Member;
import com.example.backEnd.repository.MemberRepository;
import com.example.backEnd.service.JwtService;
import com.example.backEnd.service.JwtServiceImpl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
public class AccountController {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    JwtService jwtService;

    @PostMapping("/api/account/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> params, HttpServletResponse res){
        Member member = memberRepository.findByEmailAndPassword(params.get("email"), params.get("password"));
        if(member != null){

            int id = member.getId();
            String token = jwtService.getToken("id", id);

            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);   //Javascript 에서 접근할 수 없도록 세팅
            //cookie.setMaxAge(1000*60*10);
            cookie.setPath("/");

            res.addCookie(cookie);

            return new ResponseEntity<>(id, HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/api/account/check")
    public ResponseEntity<?> check(@CookieValue(value = "token", required = false) String token){
        Claims claims = jwtService.getClaims(token);
        if(claims != null){
            int id = Integer.parseInt(claims.get("id").toString());
            return new ResponseEntity<>(id, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.OK);
    }

    @PostMapping("/api/account/logout")
    public ResponseEntity<?> logout(HttpServletResponse res) {
        Cookie cookie = new Cookie("token", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");

        res.addCookie(cookie);

        return new ResponseEntity<>(0, HttpStatus.OK);
    }

}
