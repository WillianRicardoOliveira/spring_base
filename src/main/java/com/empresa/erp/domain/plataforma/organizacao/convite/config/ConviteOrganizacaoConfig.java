package com.empresa.erp.domain.plataforma.organizacao.convite.config;

import java.time.Clock;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(
        ConviteOrganizacaoProperties.class
)
public class ConviteOrganizacaoConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}