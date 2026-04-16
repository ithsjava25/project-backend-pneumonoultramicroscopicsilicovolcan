package org.example.crimearchive.config;

import org.example.crimearchive.polis.AccountUserDetailsService;
import org.example.crimearchive.polis.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").permitAll();
                    auth.requestMatchers("/*.css", "/images/**").permitAll();
                    auth.requestMatchers("/favicon.ico").permitAll();
                    auth.requestMatchers("/index").permitAll();
                    auth.requestMatchers("/error").permitAll();

                    auth.requestMatchers("/private").hasRole("ADMIN");
                    auth.requestMatchers("/userpage").hasRole("USER");


                    auth.anyRequest().authenticated();
                })
                //Bygger inloggingsformuläret automatiskt
                //.oauth2Login(Customizer.withDefaults()) lägg till tsm med oauth2 application.properties
                .formLogin(formLogin -> formLogin.defaultSuccessUrl("/userpage"))
        .build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    AccountUserDetailsService userDetailsService(UserRepository repo){
        return new AccountUserDetailsService(repo);
    }

}
