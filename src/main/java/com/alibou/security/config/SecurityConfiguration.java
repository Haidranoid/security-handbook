package com.alibou.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import static com.alibou.security.user.Permission.*;
import static com.alibou.security.user.Role.ADMIN;
import static com.alibou.security.user.Role.MANAGER;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfiguration {

    public final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/auth/**")
                .permitAll()

                .requestMatchers("/api/v1/management/**").hasAnyRole(ADMIN.name(), MANAGER.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/management/**").hasAnyAuthority(ADMIN_READ.name(), MANAGER_READ.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/management/**").hasAnyAuthority(ADMIN_CREATE.name(), MANAGER_CREATE.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/management/**").hasAnyAuthority(ADMIN_UPDATE.name(), MANAGER_UPDATE.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/management/**").hasAnyAuthority(ADMIN_DELETE.name(), MANAGER_DELETE.name())

                /*.requestMatchers("/api/v1/admin/**").hasRole(ADMIN.name())

                .requestMatchers(HttpMethod.GET, "/api/v1/admin/**").hasAuthority(ADMIN_READ.name())
                .requestMatchers(HttpMethod.POST, "/api/v1/admin/**").hasAuthority(ADMIN_CREATE.name())
                .requestMatchers(HttpMethod.PUT, "/api/v1/admin/**").hasAuthority(ADMIN_UPDATE.name())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/admin/**").hasAuthority(ADMIN_DELETE.name())
                */

                .anyRequest()
                .authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/api/v1/auth/logout")
                .addLogoutHandler(logoutService)
                .logoutSuccessHandler((request, response, authentication) -> {
                    SecurityContextHolder.clearContext();
                })
        ;

        return http.build();
    }
}
