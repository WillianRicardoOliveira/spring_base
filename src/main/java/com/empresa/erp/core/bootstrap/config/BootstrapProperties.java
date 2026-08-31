package com.empresa.erp.core.bootstrap.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.bootstrap")
public record BootstrapProperties(
        boolean enabled,
        String organizationName,
        String organizationAdminEmail,
        String organizationAdminPassword,
        String platformAdminEmail,
        String platformAdminPassword
) {

    @Override
    public String toString() {
        return "BootstrapProperties["
                + "enabled=" + enabled
                + ", organizationName=****"
                + ", organizationAdminEmail=****"
                + ", organizationAdminPassword=****"
                + ", platformAdminEmail=****"
                + ", platformAdminPassword=****"
                + "]";
    }
}