package io.github.hrashk.news.api.authors;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;

@TestConfiguration
class TestBeans {
    @Bean
    PasswordEncoder dummyEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return rawPassword.toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Objects.equals(encodedPassword, rawPassword.toString());
            }
        };
    }
}
