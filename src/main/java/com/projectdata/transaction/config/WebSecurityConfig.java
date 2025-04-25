package com.projectdata.transaction.config;

import java.util.Arrays;

import com.projectdata.transaction.filter.JwtTokenFilter;
import com.projectdata.transaction.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/*
 * @Configured stateless session management for JWT
 * @Set up role-based access control for different API endpoints:
 * - /api/admin/** - ADMIN only
 * - /api/moderator/** - ADMIN and MODERATOR
 * - /api/user/** - All authenticated users
 * - Public endpoints remain accessible without authentication
 * @Added JWT filter to the security filter chain
 * @Added password encoder and authentication manager beans
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public WebSecurityConfig(JwtTokenFilter jwtTokenFilter, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(@NonNull HttpSecurity http) throws Exception {
        http
            .csrf(csrf-> csrf.disable())
            .cors(cors-> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/api/v1/auth/**", "/api/v1/public/**", "/actuator/**").permitAll()
                // Swagger/OpenAPI endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Admin endpoints
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                // Moderator endpoints
                .requestMatchers("/api/v1/moderator/**").hasAnyRole("ADMIN", "MODERATOR")
                // User endpoints
                .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "MODERATOR", "USER")
                // All other requests need authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setExposedHeaders(Arrays.asList("Authorization"));
        config.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
