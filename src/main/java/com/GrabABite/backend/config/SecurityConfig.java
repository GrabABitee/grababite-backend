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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.grababite.backend.repositories.UserRepository;

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
                        .roles(user.getRoles().toArray(new String[0])) // maps "ADMIN" -> ROLE_ADMIN
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    @Bean
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder
    ) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService);
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:3000",
            "https://grab-a-bite-campus-efhj.vercel.app",
            "http://localhost:8081",
            "https://lovable.dev/projects/8ae01cb5-cfdd-476a-8b56-5df71e06881b"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"
        ));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(authorize -> authorize
                // OPTIONS requests (CORS preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // Public authentication/onboarding
                .requestMatchers("/api/admin/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/onboarding/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()

                // College management (ADMIN only)
                .requestMatchers(HttpMethod.POST, "/api/colleges").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/colleges/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/colleges/**").hasRole("ADMIN")

                // Cafeteria management (ADMIN only)
                .requestMatchers(HttpMethod.POST, "/api/cafeterias").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/cafeterias/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/cafeterias/**").hasRole("ADMIN")

                // Public GET APIs
                .requestMatchers(HttpMethod.GET, "/api/colleges/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/cafeterias/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/menu-items/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/standard-menu-items/**").permitAll()

                // Standard menu item management (ADMIN only)
                .requestMatchers(HttpMethod.POST, "/api/standard-menu-items").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/standard-menu-items/bulk").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/standard-menu-items/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/standard-menu-items/**").hasRole("ADMIN")

                // User profile
                .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/users/me").authenticated()
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // Orders (basic constraints, fine-grained checks in OrderController)
                .requestMatchers(HttpMethod.POST, "/api/orders").hasRole("STUDENT")
                .requestMatchers(HttpMethod.PUT, "/api/orders/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/orders/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/orders/**").authenticated()

                // Default: everything else needs authentication
//                 .anyRequest().authenticated()
//             )
//             .httpBasic(httpBasic -> httpBasic.realmName("GrabABite Realm"));

//         return http.build();
//     }
// }
                .anyRequest().authenticated()
                )
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable);

            return http.build();
            }
        }

