package com.mlorenzo.spring6resttemplate.config;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Component
public class OAuth2ClientInterceptor  implements ClientHttpRequestInterceptor {
    public static final String CLIENT_REGISTRATION_ID = "springoauth2";

    private final OAuth2AuthorizedClientManager manager;
    private final ClientRegistration clientRegistration;

    public OAuth2ClientInterceptor(final OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager,
                                   final ClientRegistrationRepository clientRegistrationRepository) {
        manager = oAuth2AuthorizedClientManager;
        clientRegistration = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body,
                                        ClientHttpRequestExecution execution) throws IOException {
        final OAuth2AuthorizeRequest oAuth2AuthorizeRequest = OAuth2AuthorizeRequest
                .withClientRegistrationId(clientRegistration.getRegistrationId())
                .principal(createPrincipal())
                .build();
        final OAuth2AuthorizedClient oAuth2AuthorizedClient = manager.authorize(oAuth2AuthorizeRequest);
        if(oAuth2AuthorizedClient == null)
            throw new IllegalStateException("Missing credentials");
        request.getHeaders().add(HttpHeaders.AUTHORIZATION,
                String.format("Bearer %s", oAuth2AuthorizedClient.getAccessToken().getTokenValue()));
        return execution.execute(request, body);
    }

    private Authentication createPrincipal() {
        return new Authentication() {

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return Collections.emptySet();
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return this;
            }

            @Override
            public Object getPrincipal() {
                return null;
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }

            @Override
            public String getName() {
                return clientRegistration.getClientId();
            }
        };
    }
}
