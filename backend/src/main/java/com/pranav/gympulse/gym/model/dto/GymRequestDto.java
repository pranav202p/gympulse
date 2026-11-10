package com.pranav.gympulse.gym.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class GymRequestDto {

    private String name;

    private String address;

    private String city;

    private String state;

    private String postalCode;

    private String phone;

    private String email;
}
