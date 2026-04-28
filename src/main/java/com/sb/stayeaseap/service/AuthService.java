package com.sb.stayeaseap.service;

import com.sb.stayeaseap.model.User;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sb.stayeaseap.Validator.UserValidator;
import com.sb.stayeaseap.repository.UserRepository;

@Service
public class AuthService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserValidator userValidator;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserValidator userValidator){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userValidator = userValidator;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    
        return org.springframework.security.core.userdetails.User.
        withUsername(user.getEmail())
        .password(user.getPassword())
        .roles("USER")
        .build();
    }

    public void register(String name, String email, String password, String confirmPassword) {
        userValidator.validateRegistration(name, email, password, confirmPassword);
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
