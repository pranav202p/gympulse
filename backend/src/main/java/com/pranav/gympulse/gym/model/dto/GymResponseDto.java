package com.pranav.gympulse.gym.model.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class GymResponseDto {

    private String name;

    private String address;

    private String city;

    private String state;

    private String postalCode;

    private String phone;

    private String email;
}
