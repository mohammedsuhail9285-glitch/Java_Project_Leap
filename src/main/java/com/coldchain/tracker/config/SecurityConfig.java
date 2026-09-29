package com.coldchain.tracker.config;

import com.coldchain.tracker.security.CustomUserDetailsService;
import com.coldchain.tracker.security.JwtAuthenticationEntryPoint;
import com.coldchain.tracker.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration enabling stateless JWT authentication and role-based access.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public web dashboard & Swagger UI docs
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/favicon.ico",
                                "/static/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/v3/api-docs/**",
                                "/api-docs/**"
                        ).permitAll()
                        // Public Authentication endpoints
                        .requestMatchers("/api/v1/auth/**", "/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/users", "/users", "/api/v1/users/login", "/users/login").permitAll()
                        // Public dashboard summary (helpful for kiosks and monitors)
                        .requestMatchers(HttpMethod.GET, "/api/v1/dashboard/**", "/dashboard/**").permitAll()
                        // Admin-only management endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/users/**", "/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/v1/vehicles/**", "/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/vehicles/**", "/vehicles/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/vehicles/**", "/vehicles/**").hasRole("ADMIN")
                        // Operator & Admin accessible endpoints
                        .requestMatchers(HttpMethod.GET, "/api/v1/vehicles/**", "/vehicles/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/v1/shipments/**", "/shipments/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/v1/temperature-logs/**", "/temperature-logs/**", "/temperature-log/**", "/api/v1/temperature-log/**").hasAnyRole("ADMIN", "OPERATOR")
                        .requestMatchers("/api/v1/alerts/**", "/alerts/**").hasAnyRole("ADMIN", "OPERATOR")
                        // Fallback: any other request must be authenticated
                        .anyRequest().authenticated()
                );

        http.authenticationProvider(authenticationProvider());
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
