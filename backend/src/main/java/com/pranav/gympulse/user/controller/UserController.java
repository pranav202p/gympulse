package com.pranav.gympulse.user.controller;

import com.pranav.gympulse.common.dto.ApiResponse;
import com.pranav.gympulse.user.model.dto.UserRequestDto;
import com.pranav.gympulse.user.model.dto.UserResponseDto;
import com.pranav.gympulse.user.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @GetMapping("/me")
    public ApiResponse<UserResponseDto> getUser(Authentication authentication){

        String email=authentication.getName();

        UserResponseDto responseDto=userService.getUser(email);

        return  new ApiResponse<>(true,"User Fetched sucessfully",responseDto);

    }

    @PutMapping("/me")
    public ApiResponse<UserResponseDto> updateUser(@RequestBody UserRequestDto requestDto,Authentication authentication){

        String email=authentication.getName();

        UserResponseDto  updatedUserDeatils=userService.updateUser(requestDto,email);


        return  new ApiResponse<>(true,"User updated sucessfully",updatedUserDeatils);

    }


}