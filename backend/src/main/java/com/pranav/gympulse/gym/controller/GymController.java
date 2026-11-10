package com.pranav.gympulse.gym.controller;

import com.pranav.gympulse.common.dto.ApiResponse;
import com.pranav.gympulse.gym.model.dto.GymRequestDto;
import com.pranav.gympulse.gym.model.dto.GymResponseDto;
import com.pranav.gympulse.gym.service.GymService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gyms")
public class GymController {

    private final GymService gymService;

    public GymController(GymService gymService) {
        this.gymService = gymService;
    }

    @GetMapping
    public ApiResponse<List<GymResponseDto>> getGyms(){

        List<GymResponseDto> gyms=gymService.getGyms();

        ApiResponse<List<GymResponseDto>> response=new ApiResponse<>(true,"fetched gym sucessfully",gyms);

        return response;

    }

    @PostMapping
    public ApiResponse<GymResponseDto> addGym(@RequestBody GymRequestDto gymRequestDto){

        GymResponseDto gym=gymService.addGym(gymRequestDto);

         ApiResponse<GymResponseDto> newGym=new ApiResponse<>(true," gym added sucessfully",gym);

         return newGym;

    }

    @GetMapping("/{id}")
    public ApiResponse<GymResponseDto> getGym(@PathVariable Long id){

        GymResponseDto getGym=gymService.getGym(id);

        System.out.println("Gym Details: " + getGym);

        ApiResponse<GymResponseDto> gym=new ApiResponse<>(true," gym fetched sucessfully",getGym);

        return  gym;

    }
}
