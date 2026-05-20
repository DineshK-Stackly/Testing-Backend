package com.stackly.auth.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.stackly.auth.repository.SsoRepository;
import com.stackly.common.entity.SsoUser;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    @Autowired
    private SsoRepository ssoRepo;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest)
            throws OAuth2AuthenticationException {

        OidcUser oidcUser = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes = oidcUser.getAttributes();

        // 🔥 UNIFIED EXTRACTION
        String email = extractEmail(provider, attributes);
        String name = extractName(provider, attributes);
        String providerId = extractProviderId(provider, attributes);

        if (providerId == null) {
            throw new OAuth2AuthenticationException("Provider ID is null");
        }

        Optional<SsoUser> existingUser =
                ssoRepo.findByProviderAndProviderId(provider, providerId);

        SsoUser user;

        if (existingUser.isPresent()) {
            user = existingUser.get();
        } else {
            user = SsoUser.builder()
                    .email(email)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .createdAt(LocalDateTime.now())
                    .build();

            ssoRepo.save(user);
        }

        return oidcUser;
    }

    // ================= HELPERS =================

    private String extractEmail(String provider, Map<String, Object> attr) {
        return (String) attr.getOrDefault("email",
                attr.getOrDefault("preferred_username", null));
    }

    private String extractName(String provider, Map<String, Object> attr) {
        return (String) attr.getOrDefault("name",
                attr.getOrDefault("given_name", "User"));
    }

    private String extractProviderId(String provider, Map<String, Object> attr) {
        return (String) attr.get("sub"); // works for all 3
    }
}