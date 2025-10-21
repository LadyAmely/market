package com.example.market.integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
class ContainerConfig {

    @Bean
    @ServiceConnection
    public PostgreSQLContainer<?> postgisContainer() {
        DockerImageName postgisImage = DockerImageName.parse("postgis/postgis:17-3.5")
                .asCompatibleSubstituteFor("postgres");

        return new PostgreSQLContainer<>(postgisImage)
                .withDatabaseName("test_db")
                .withUsername("postgres")
                .withPassword("postgres");
    }
}
