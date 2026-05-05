package com.sb.stayeaseap.config;

import com.sb.stayeaseap.service.AuthService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(AuthService authService, PasswordEncoder passwordEncoder){
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }


    @Bean
    public DaoAuthenticationProvider authProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authProvider())
        .authorizeHttpRequests(auth -> auth.requestMatchers("/", "/search", "/hotel/**", "/room/**",
                                 "/reviews", "/about", "/contact",
                                 "/login", "/register",
                                 "/css/**", "/images/**", "/js/**").permitAll()
                                .requestMatchers("/booking/**", "/dashboard/**", "/reviews/submit").authenticated()
                                .anyRequest().authenticated()
        )
        .formLogin(form -> form.loginPage("/login").defaultSuccessUrl("/", true).failureUrl("/login?error").permitAll())
        .logout(logout -> logout.logoutUrl("/logout").logoutSuccessUrl("/login?logout").permitAll());

        return http.build();
    }
}
