package com.pranav.gympulse.auth.service;

import com.pranav.gympulse.auth.models.dto.AuthResponseDto;
import com.pranav.gympulse.auth.models.dto.LoginRequestDto;
import com.pranav.gympulse.auth.models.dto.RegisterRequestDto;
import org.springframework.stereotype.Component;

@Component
public interface AuthService {


    AuthResponseDto createUser(RegisterRequestDto requestDto);

    AuthResponseDto login(LoginRequestDto requestDto);
}
