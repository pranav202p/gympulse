package com.pranav.gympulse.user.service;

import com.pranav.gympulse.auth.exception.UserNotFoundException;
import com.pranav.gympulse.user.model.dto.UserRequestDto;
import com.pranav.gympulse.user.model.dto.UserResponseDto;
import com.pranav.gympulse.user.model.entity.User;
import com.pranav.gympulse.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserServiceImpl{

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserResponseDto getUser(String email) {


        User user=userRepository.findByEmail(email);



        return new UserResponseDto(user.getName(),user.getPhoneno(),user.getEmail());

    }

    @Override
    public UserResponseDto updateUser(UserRequestDto requestDto, String email) {

        User user=userRepository.findByEmail(email);

        if(user == null){
            throw  new UserNotFoundException("Usr not Found");
        }
        user.setName(requestDto.getName());
        user.setPhoneno(requestDto.getPhoneno());
        user.setEmail(requestDto.getEmail());

        User updatedUser=userRepository.save(user);

        return new UserResponseDto(
                user.getName(),
                user.getPhoneno(),
                user.getEmail()
        );

    }
}
