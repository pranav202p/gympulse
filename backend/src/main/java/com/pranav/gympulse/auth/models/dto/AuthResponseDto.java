package com.pranav.gympulse.auth.models.dto;

public class AuthResponseDto {

    public long Id;

    public String name;

    public String email;

    public String token;

    public AuthResponseDto(long id, String name, String email, String  token) {
        Id = id;
        this.name = name;
        this.email = email;
        this.token=token;
    }

    public AuthResponseDto(long id, String name, String email) {
        Id = id;
        this.name = name;
        this.email = email;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setId(long id) {
        Id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
