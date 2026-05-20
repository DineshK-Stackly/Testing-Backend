package com.stackly.auth.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public Map<String, String> dashboard(Authentication authentication) {
        String email = resolveEmail(authentication);
        String role = authentication.getAuthorities().stream()
                .findFirst()
                .map(authority -> authority.getAuthority())
                .orElse("ROLE_USER");

        Map<String, String> response = new LinkedHashMap<>();
        response.put("message", "Successfully logged in");
        response.put("email", email);
        response.put("role", role);
        response.put("authType", authentication.getClass().getSimpleName());
        return response;
    }

    private String resolveEmail(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof OidcUser) {
            OidcUser oidcUser = (OidcUser) principal;
            String email = oidcUser.getEmail();
            if (email != null && !email.isBlank()) {
                return email;
            }

            String preferredUsername = oidcUser.getAttribute("preferred_username");
            if (preferredUsername != null && !preferredUsername.isBlank()) {
                return preferredUsername;
            }
        }

        return authentication.getName();
    }
}
