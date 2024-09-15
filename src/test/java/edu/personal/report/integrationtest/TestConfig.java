package edu.personal.report.integrationtest;

import edu.personal.report.model.User;
import edu.personal.report.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            User user = new User(null, "username", "pass", "user");
            userRepository.save(user);
        };
    }
}

