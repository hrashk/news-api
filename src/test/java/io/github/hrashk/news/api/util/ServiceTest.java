package io.github.hrashk.news.api.util;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;

@DataJpaTest(properties = {"logging.level.org.hibernate.orm.jdbc.bind=trace"})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = PostgreSQLInitializer.class)
@Import(DataSeeder.class)
public abstract class ServiceTest {
    @Autowired
    protected DataSeeder seeder;

    @BeforeEach
    void seedSamples() {
        seeder.seed(10);
    }
}
