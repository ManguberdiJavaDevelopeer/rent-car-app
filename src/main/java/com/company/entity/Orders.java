package com.company.entity;

import com.company.enums.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Orders {
    Integer userId;
    Integer carId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    OrderStatus status;
    String passport;
    String driverLicense;
    String region;
    Double totalPrice;
}
