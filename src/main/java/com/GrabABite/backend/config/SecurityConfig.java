package com.grababite.backend.config;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.grababite.backend.repositories.UserRepository;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JwtFilter jwtFilter;

    public SecurityConfig(UserRepository userRepository, JwtFilter jwtFilter) {
        this.userRepository = userRepository;
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .map(user -> org.springframework.security.core.userdetails.User.builder()
                        .username(user.getEmail())
                        .password(user.getPassword())
                        .roles(user.getRoles().toArray(new String[0]))
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",
                "https://grab-a-bite-campus-efhj.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization", "Content-Type"
        ));
        configuration.setAllowCredentials(false); // JWT = no cookies
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(authorize -> authorize

                // Public routes
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/admin/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/onboarding/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/colleges/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cafeterias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menu-items/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/standard-menu-items/**").permitAll()

                // ADMIN routes
                .requestMatchers(HttpMethod.POST, "/api/colleges").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/colleges/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/colleges/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/cafeterias").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/cafeterias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/cafeterias/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/standard-menu-items").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/standard-menu-items/bulk").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/standard-menu-items/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/standard-menu-items/**").hasRole("ADMIN")

                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // STUDENT routes
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("STUDENT")

                // Authenticated
                .requestMatchers("/api/orders/**").authenticated()
                .requestMatchers("/api/users/me").authenticated()

                .anyRequest().authenticated()
            );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
