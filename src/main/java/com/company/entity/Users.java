package com.company.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {
    String firstName;
    String lastName;
    String phoneNumber;
    String email;
    String region;
    String address;
    String password;

    public Users(String first_name, String last_name, String phone_number) {
        this.firstName=first_name;
        this.lastName=last_name;
        this.phoneNumber=phone_number;
    }
}
