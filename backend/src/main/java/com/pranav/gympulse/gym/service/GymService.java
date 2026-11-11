package com.pranav.gympulse.gym.service;

import com.pranav.gympulse.gym.model.dto.GymRequestDto;
import com.pranav.gympulse.gym.model.dto.GymResponseDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface GymService {

    List<GymResponseDto> getGyms();

    GymResponseDto addGym(GymRequestDto gymRequestDto);

    GymResponseDto getGym(Long id);

    GymResponseDto editGym(GymRequestDto gymRequestDto);

    void deleteGym(Long Id);
}
