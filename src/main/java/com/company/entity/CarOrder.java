package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarOrder {
    Integer orderId;
    Car car;
    Orders order;
    Photo photos;
    LocalDateTime createdTime;
    String userPhoneNumber;
}
