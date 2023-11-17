package io.github.hrashk.news.api;

import io.github.hrashk.news.api.seeder.DataSeeder;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
@Import(DataSeeder.class)
public class TestNewsApiApplication {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        return new PostgreSQLContainer<>(DockerImageName.parse("postgres:16-alpine"))
                .withInitScript("init.sql");
    }

    @Bean
    CommandLineRunner seedSampleData(DataSeeder generator) {
        return args -> generator.seed(10);
    }

    public static void main(String[] args) {
        SpringApplication.from(NewsApiApplication::main).with(TestNewsApiApplication.class).run(args);
    }

}
