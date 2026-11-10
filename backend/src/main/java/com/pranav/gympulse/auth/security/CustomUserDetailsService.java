package com.pranav.gympulse.auth.security;

import com.pranav.gympulse.user.model.entity.User;
import com.pranav.gympulse.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
       User user=repository.findByEmail(email);

       if(user==null){
           throw  new UsernameNotFoundException("Invalid email or password");

       }
       return new CustomUserDetails(user);
    }
}
