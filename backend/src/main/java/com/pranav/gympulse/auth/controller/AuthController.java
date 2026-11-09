package com.pranav.gympulse.auth.controller;

import com.pranav.gympulse.auth.models.dto.AuthResponseDto;
import com.pranav.gympulse.auth.models.dto.LoginRequestDto;
import com.pranav.gympulse.auth.models.dto.RegisterRequestDto;
import com.pranav.gympulse.auth.service.AuthService;
import com.pranav.gympulse.common.dto.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {


    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ApiResponse<AuthResponseDto> registerUser(@RequestBody RegisterRequestDto requestDto){

       AuthResponseDto result=authService.createUser(requestDto);

        ApiResponse<AuthResponseDto> response=new ApiResponse<>(true,"User Registered Sucessfully",result);

        return  response;

    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDto> loginUser(@RequestBody LoginRequestDto requestDto){

        AuthResponseDto result=authService.login(requestDto);

        ApiResponse<AuthResponseDto> response=new ApiResponse<>(true,"User Login Sucessfully",result);

        return  response;

    }

}
