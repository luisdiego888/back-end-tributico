package com.project.demo.logic.entity.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {
        private final AuthenticationProvider authenticationProvider;
        private final JwtAuthenticationFilter jwtAuthenticationFilter;
        private final CustomOAuth2SuccesHandler customOAuth2SuccesHandler;
        private final CustomOAuth2UserService customOAuth2UserService;

        public SecurityConfiguration(AuthenticationProvider authenticationProvider,
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        CustomOAuth2SuccesHandler customOAuth2SuccesHandler,
                        CustomOAuth2UserService customOAuth2UserService) {
                this.authenticationProvider = authenticationProvider;
                this.jwtAuthenticationFilter = jwtAuthenticationFilter;
                this.customOAuth2SuccesHandler = customOAuth2SuccesHandler;
                this.customOAuth2UserService = customOAuth2UserService;
        }


        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                        .csrf().disable()
                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                        .authorizeHttpRequests((authorize) -> authorize
                                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/auth/**").permitAll()
                                .requestMatchers(HttpMethod.GET, "/auth/google").permitAll()
                                .requestMatchers(HttpMethod.PUT, "/auth/block").permitAll()
                                .requestMatchers("/ws/**", "/topic/**", "/queue/**", "/app/**", "/user/**").permitAll()
                                .anyRequest().authenticated())

                        .sessionManagement(session -> session
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                        .authenticationProvider(authenticationProvider)
                        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                        .oauth2Login(oauth2 -> oauth2
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService))
                                .successHandler(customOAuth2SuccesHandler));

                return http.build();
        }

        @Bean
        public CorsConfigurationSource corsConfigurationSource() {
                CorsConfiguration configuration = new CorsConfiguration();
                configuration.setAllowedOriginPatterns(List.of("*"));
                configuration.setAllowedMethods(List.of("*"));
                configuration.setAllowedHeaders(List.of("*"));
                configuration.setAllowCredentials(true);

                configuration.setExposedHeaders(List.of("Access-Control-Allow-Origin"));

                UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                source.registerCorsConfiguration("/**", configuration);
                return source;
        }

        @Bean
        public HttpFirewall httpFirewall() {
                StrictHttpFirewall firewall = new StrictHttpFirewall();
                firewall.setAllowSemicolon(true);
                return firewall;
        }

}

