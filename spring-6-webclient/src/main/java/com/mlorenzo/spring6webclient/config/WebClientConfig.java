package com.mlorenzo.spring6webclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.reactive.function.client.WebClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;

@Configuration
public class WebClientConfig {
    private final String rootUrl;
    private final ReactiveOAuth2AuthorizedClientManager authorizedClientManager;

    public WebClientConfig(@Value("${webclient.rootUrl}") final String rootUrl,
                           final ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        this.rootUrl = rootUrl;
        this.authorizedClientManager = authorizedClientManager;
    }

    @Bean
    public WebClientCustomizer customize() {
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2ClientCredentialsFilter =
                new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
        oauth2ClientCredentialsFilter.setDefaultClientRegistrationId("springoauth2");
        return webClientBuilder -> webClientBuilder
                .filter(oauth2ClientCredentialsFilter)
                .baseUrl(rootUrl);
    }
}
