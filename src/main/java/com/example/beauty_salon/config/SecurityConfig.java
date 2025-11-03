package com.example.beauty_salon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()  // всички страници са достъпни
                )
                .csrf(csrf -> csrf.disable())  // изключва CSRF за разработка
                .formLogin(form -> form.disable()) // маха login формата
                .httpBasic(basic -> basic.disable()); // маха Basic Auth
        return http.build();
    }
}
