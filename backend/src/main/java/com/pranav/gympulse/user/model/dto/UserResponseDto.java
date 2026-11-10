package com.pranav.gympulse.user.model.dto;

public class UserResponseDto {

    public String name;

    public String phoneno;

    public String email;

    public UserResponseDto(String name, String phoneno, String email) {
        this.name = name;
        this.phoneno = phoneno;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getPhoneno() {
        return phoneno;
    }

    public String getEmail() {
        return email;
    }
}
