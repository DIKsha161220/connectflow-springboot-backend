package com.connectflow.backend.config;

import com.connectflow.backend.dto.ApiResponse;
import com.connectflow.backend.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            ObjectMapper objectMapper) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) ->
                                writeError(response, objectMapper, 401, "Authentication is required"))
                        .accessDeniedHandler((request, response, exception) ->
                                writeError(response, objectMapper, 403, "You are not authorized to perform this action")))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/institutes/*/calls")
                            .hasAnyRole("ADMINISTRATIVE", "CALLING_TEAM")
                        .requestMatchers(HttpMethod.GET, "/api/institutes/*/contacts")
                            .hasAnyRole("ADMINISTRATIVE", "CALLING_TEAM", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/institutes", "/api/institutes/*")
                            .hasAnyRole("ADMINISTRATIVE", "CALLING_TEAM", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.POST, "/api/institutes")
                            .hasRole("ADMINISTRATIVE")
                        .requestMatchers(HttpMethod.POST, "/api/calls", "/api/call-logs")
                            .hasAnyRole("ADMINISTRATIVE", "CALLING_TEAM", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/calls/*", "/api/call-logs/*")
                            .hasAnyRole("ADMINISTRATIVE", "CALLING_TEAM", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/calls", "/api/calls/*", "/api/call-logs", "/api/call-logs/*")
                            .hasAnyRole("ADMINISTRATIVE", "CALLING_TEAM")
                        .requestMatchers(HttpMethod.POST, "/api/visits")
                            .hasAnyRole("ADMINISTRATIVE", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.PUT, "/api/visits/*")
                            .hasAnyRole("ADMINISTRATIVE", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.GET, "/api/visits", "/api/visits/*")
                            .hasAnyRole("ADMINISTRATIVE", "ON_FIELD_EMPLOYEE")
                        .requestMatchers(HttpMethod.PATCH, "/api/visits/*/status")
                            .hasAnyRole("ADMINISTRATIVE", "ON_FIELD_EMPLOYEE")
                        .requestMatchers("/api/follow-ups/**", "/api/dashboard/**",
                                "/api/employees/**", "/api/roles/**", "/api/audit-logs/**",
                                "/api/institute-contacts/**")
                            .hasRole("ADMINISTRATIVE")
                        .anyRequest().authenticated())
                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static void writeError(
            jakarta.servlet.http.HttpServletResponse response,
            ObjectMapper objectMapper,
            int status,
            String message) throws IOException {
        response.setStatus(status);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), ApiResponse.failure(message));
    }
}
