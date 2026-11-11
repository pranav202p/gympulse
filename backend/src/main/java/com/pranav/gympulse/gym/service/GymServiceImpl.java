package com.pranav.gympulse.gym.service;

import com.pranav.gympulse.gym.exception.GymNotFoundException;
import com.pranav.gympulse.gym.model.dto.GymRequestDto;
import com.pranav.gympulse.gym.model.dto.GymResponseDto;
import com.pranav.gympulse.gym.model.entity.Gym;
import com.pranav.gympulse.gym.repository.GymRepository;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;


@Service
public class GymServiceImpl implements  GymService{

    private final GymRepository gymRepository;

    public GymServiceImpl(GymRepository gymRepository) {
        this.gymRepository = gymRepository;
    }

    @Override
    public List<GymResponseDto> getGyms() {

        return gymRepository.findAll()
                .stream()
                .map(gym -> new GymResponseDto(
                        gym.getName(),
                        gym.getAddress(),
                        gym.getCity(),
                        gym.getState(),
                        gym.getPostalCode(),
                        gym.getPhone(),
                        gym.getEmail()
                ))
                .toList();
    }

    @Override
    public GymResponseDto addGym(GymRequestDto gymRequestDto) {
        Gym newGym=new Gym(
                gymRequestDto.getName(),
                gymRequestDto.getAddress(),
                gymRequestDto.getCity(),
                gymRequestDto.getState(),
                gymRequestDto.getPostalCode(),
                gymRequestDto.getPhone(),
                gymRequestDto.getEmail()
        );
        gymRepository.save(newGym);

        return new GymResponseDto(newGym.getName(),
                newGym.getAddress(),
                newGym.getCity(),
                newGym.getState(),
                newGym.getPostalCode(),
                newGym.getPhone(),
                newGym.getEmail());
    }

    @Override
    public GymResponseDto getGym(Long id) {
       Gym getGym=gymRepository.findById(id)
               .orElseThrow(()->new GymNotFoundException("Gym not found"));

        return new GymResponseDto(getGym.getName(),
                getGym.getAddress(),
                getGym.getCity(),
                getGym.getState(),
                getGym.getPostalCode(),
                getGym.getPhone(),
                getGym.getEmail());
    }

    @Override
    public GymResponseDto editGym(GymRequestDto gymRequestDto) {
        Gym newGym=new Gym(
                gymRequestDto.getName(),
                gymRequestDto.getAddress(),
                gymRequestDto.getCity(),
                gymRequestDto.getState(),
                gymRequestDto.getPostalCode(),
                gymRequestDto.getPhone(),
                gymRequestDto.getEmail()
        );
        gymRepository.save(newGym);

        return new GymResponseDto(newGym.getName(),
                newGym.getAddress(),
                newGym.getCity(),
                newGym.getState(),
                newGym.getPostalCode(),
                newGym.getPhone(),
                newGym.getEmail());
    }

    @Override
    public void deleteGym(Long Id) {

        Gym deleteGym=gymRepository.findById(Id).orElseThrow(()-> new GymNotFoundException("Gym not found"));
        gymRepository.delete(deleteGym);
    }
}
