package com.grababite.backend.config;

import com.grababite.backend.repositories.UserRepository;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
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
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Allow your frontend origins
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://grab-a-bite-campus-efhj.vercel.app"));
        // Allow all necessary HTTP methods
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Allow specific headers
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Allow credentials (like cookies, HTTP authentication)
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // Apply this CORS configuration to all paths
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for stateless REST APIs
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Apply CORS configuration
            .authorizeHttpRequests(authorize -> authorize
                // NEW: Allow OPTIONS requests for all paths to handle CORS preflight requests
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Publicly accessible endpoints for initial setup
                .requestMatchers("/api/admin/register").permitAll()
                .requestMatchers("/api/onboarding/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll() // Allow login endpoint

                // College and Cafeteria creation/management - Restricted to ADMIN
                .requestMatchers(HttpMethod.POST, "/api/colleges").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/colleges/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/colleges/**").hasRole("ADMIN")

                .requestMatchers(HttpMethod.POST, "/api/cafeterias").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/cafeterias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/cafeterias/**").hasRole("ADMIN")

                // Allow anyone to GET college, cafeteria, and standard menu item lists/details (read-only access)
                .requestMatchers(HttpMethod.GET, "/api/colleges/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cafeterias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menu-items/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/standard-menu-items/**").permitAll()

                // Menu Item Management (handled by @PreAuthorize in MenuItemController)
                // Standard Menu Item Management (Admin-only)
                .requestMatchers(HttpMethod.POST, "/api/standard-menu-items").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/standard-menu-items/bulk").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/standard-menu-items/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/standard-menu-items/**").hasRole("ADMIN")

                // User Management Endpoints - Restricted to ADMIN
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // App Settings Endpoints - Restricted to ADMIN (using method-level security via @PreAuthorize)
                // All other requests require authentication (catch-all for future endpoints)
                .anyRequest().authenticated()
            )
            .httpBasic(httpBasic -> httpBasic.realmName("GrabABite Realm")); // Enable HTTP Basic authentication

        return http.build();
    }
}
