package com.stackly.auth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.stackly.auth.security.SessionLoginSuccessHandler;
import com.stackly.auth.service.CustomOAuth2UserService;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Autowired
	 private CustomOAuth2UserService oauthUserService;

	@Autowired
	private SessionLoginSuccessHandler sessionLoginSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) // Disabled for Postman testing
        .authorizeHttpRequests(auth -> auth
        		.requestMatchers(
        				"/api/auth/**",
        				"/oauth2/**",
        				"/login",
        				"/error").permitAll()
        		.anyRequest().authenticated()
        		)
        // Enables username/password login and redirects to /dashboard on success
        .formLogin(form -> form
        		.usernameParameter("email")
        		.passwordParameter("password")
        		.successHandler(sessionLoginSuccessHandler)
        		.permitAll()
        		)
        .oauth2Login(oauth -> oauth
        		.userInfoEndpoint(user -> user
        				.oidcUserService(oauthUserService)
        				)
        		.successHandler(sessionLoginSuccessHandler)
        		)
        .logout(logout -> logout
        		.logoutSuccessUrl("/login")
        		);
    
    return http.build();
    }

}