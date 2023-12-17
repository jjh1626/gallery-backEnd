package com.example.backEnd.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDto {
    private String name;
    private String address;
    private String payment;
    private String cardNumber;
    private String items;

}
