package com.medinet.infrastructure.security;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder,
            UserDetailsService userDetailService
    )
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "true", matchIfMissing = true)
    SecurityFilterChain securityEnabled(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .requestMatchers("/login",
                                        "/",
                                        "/register/**",
                                        "/policy",
                                        "/images/avatar_profile_page.jpg",
                                        "/images/doctor.png",
                                        "/error",
                                        "/booking/**",
                                        "/main.css",
                                        "/mainpage.css",
                                        "/avatar_profile_page.jpg",
                                        "/myAccountDoctor.css",
                                        "/doctor_page.css",
                                        "/invoice/**",
                                        "/password/**",
                                        "/api/**",
                                        "/swagger-ui/**",
                                        "/v3/**",
                                        "/myAccount.css").permitAll()
                                .requestMatchers("/doctor/**",
                                        "appointment/approve/**"
                                        , "/user/**").hasAnyAuthority("DOCTOR")
                                .requestMatchers(
                                        "/request/**",
                                        "/booking?page/**",
                                        "/account/**",
                                        "password/**",
                                        "/specialist/details/**",
                                        "/opinion/**").hasAnyAuthority("PATIENT")

                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/booking")
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .invalidateHttpSession(true)
                                .deleteCookies("JSESSIONID")
                                .permitAll());

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(value = "spring.security.enabled", havingValue = "false")
    SecurityFilterChain securityDisabled(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests()
                .anyRequest()
                .permitAll();

        return http.build();
    }

}
