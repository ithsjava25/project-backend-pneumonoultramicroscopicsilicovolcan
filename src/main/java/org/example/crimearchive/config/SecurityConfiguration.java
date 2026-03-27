package org.example.crimearchive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/static/**").permitAll();
                    auth.requestMatchers("/favicon.ico").permitAll();
                    auth.requestMatchers("/index").permitAll();
                    auth.requestMatchers("/error").permitAll();


                    auth.anyRequest().authenticated();
                })
                //Bygger inloggingsformuläret automatiskt
                .formLogin(formLogin -> formLogin.defaultSuccessUrl("/private"))
                .build();
    }
}
