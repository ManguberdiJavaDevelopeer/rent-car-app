package com.company.entity;

import com.company.enums.CarStatus;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Car {
    private String carModel;
    private String carNumber;
    private Integer countOfPlace;
    private String carColor;
    private String carRegion;
    private Integer carYear;
    private String pricePerDay;
    private String additionInfo;
    private CarStatus carStatus;

    public Car(String model, String carNumber, String pricePerHour) {
        carModel=model;
        this.carNumber=carNumber;
        this.pricePerDay=pricePerHour;
    }
}
