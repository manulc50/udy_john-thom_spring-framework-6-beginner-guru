package com.mlorenzo.spring6resttemplate.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.client.RestTemplateBuilderConfigurer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.*;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Configuration
public class RestTemplateBuilderConfig {

    @Value("${rest.template.rootUrl}")
    private String rootUrl;

    // Se comentan porque el servidor de los recursos ya no usa Autenticación Básica
    /*@Value("${rest.template.username}")
    private String username;*/

    /*@Value("${rest.template.password}")
    private String password;*/

    @Bean
    public OAuth2AuthorizedClientManager oAuth2AuthorizedClientManager(
            final ClientRegistrationRepository clientRegistrationRepository,
            final OAuth2AuthorizedClientService oAuth2AuthorizedClientService) {
        final OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
                .clientCredentials()
                .build();
        final AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository,
                        oAuth2AuthorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    @Bean
    public RestTemplateBuilder restTemplateBuilder(final RestTemplateBuilderConfigurer configurer,
                                                   final OAuth2ClientInterceptor oAuth2ClientInterceptor) {
        // La ejecución falla si no se cumple esta condición, es decir, "rootUrl" tiene que existir
        assert rootUrl != null;
        return configurer.configure(new RestTemplateBuilder())
                .additionalInterceptors(oAuth2ClientInterceptor)
                .uriTemplateHandler(new DefaultUriBuilderFactory(rootUrl));
                // Se comenta porque el servidor de los recursos ya no usa Autenticación Básica
                //.basicAuthentication(username, password);
    }
}
