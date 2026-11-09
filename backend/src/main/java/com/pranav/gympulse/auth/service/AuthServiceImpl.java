package com.pranav.gympulse.auth.service;

import com.pranav.gympulse.auth.models.dto.AuthResponseDto;
import com.pranav.gympulse.auth.models.dto.LoginRequestDto;
import com.pranav.gympulse.auth.models.dto.RegisterRequestDto;
import com.pranav.gympulse.user.models.entity.User;
import com.pranav.gympulse.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public AuthResponseDto createUser(RegisterRequestDto requestDto) {
        
        User user=new User(
                requestDto.getName(),
                requestDto.getPhoneno(),
                requestDto.getEmail(),
                passwordEncoder.encode(requestDto.getPassword())
        );

        User result=userRepository.save(user);

        return new AuthResponseDto(
                 result.getId(),
                 result.getName(),
                 result.getEmail()
        );
    }

    @Override
    public AuthResponseDto login(LoginRequestDto requestDto) {

        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );

        if (authentication.isAuthenticated()) {

            User user = userRepository.findByEmail(authentication.getName());

            return new AuthResponseDto(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    jwtService.generateToken(user.getEmail())

            );
        }
        else {

            return null;
        }
    }
}
