package com.pranav.gympulse.user.service;

import com.pranav.gympulse.user.model.dto.UserRequestDto;
import com.pranav.gympulse.user.model.dto.UserResponseDto;
import org.springframework.stereotype.Component;

@Component
public interface UserServiceImpl {


    UserResponseDto getUser(String email);

    UserResponseDto updateUser(UserRequestDto requestDto, String email);
}
