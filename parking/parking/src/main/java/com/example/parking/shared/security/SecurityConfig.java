package com.example.parking.shared.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final RestAccessDeniedHandler restAccessDeniedHandler;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter,
            RestAuthenticationEntryPoint restAuthenticationEntryPoint,
            RestAccessDeniedHandler restAccessDeniedHandler
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.restAuthenticationEntryPoint = restAuthenticationEntryPoint;
        this.restAccessDeniedHandler = restAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(restAuthenticationEntryPoint)
                        .accessDeniedHandler(restAccessDeniedHandler)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/register").permitAll()

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/parking-slots").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/parking-slots/available").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/parking-slots").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/parking-slots/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/parking-slots/*").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/parking-sessions/check-in").hasRole("STAFF")
                        .requestMatchers(HttpMethod.POST, "/api/parking-sessions/check-out").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/parking-sessions/history").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/parking-sessions").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/parking-sessions/active").hasRole("STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/parking-sessions/active/by-plate").hasRole("STAFF")

                        .requestMatchers(HttpMethod.GET, "/api/invoices/my").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/invoices").hasAnyRole("STAFF", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/invoices/my").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/invoices").hasAnyRole("STAFF", "ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/payments").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/payments/my-invoices").hasRole("USER")

                        .requestMatchers(HttpMethod.POST, "/api/incidents/my").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/incidents/my").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/incidents").hasAnyRole("STAFF", "ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/incidents/*/status").hasAnyRole("STAFF", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/vehicles/register").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/vehicles/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/vehicles/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/vehicles/*/block").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/vehicles/*/unblock").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/vehicles/search").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.GET, "/api/vehicles").hasAnyRole("ADMIN", "STAFF", "USER")
                        .requestMatchers(HttpMethod.POST, "/api/vehicles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/vehicles/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/vehicles/*").hasRole("ADMIN")

                        .requestMatchers("/api/pricing/**").hasRole("STAFF")

                        .anyRequest().authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PUT",
                "PATCH",
                "DELETE",
                "OPTIONS"
        ));

        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}