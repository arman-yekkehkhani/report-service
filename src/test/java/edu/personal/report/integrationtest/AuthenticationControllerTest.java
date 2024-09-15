package edu.personal.report.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.personal.report.controller.auth.LoginUserDto;
import edu.personal.report.controller.auth.RegisterUserDto;
import edu.personal.report.model.User;
import edu.personal.report.repository.UserRepository;
import edu.personal.report.util.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationControllerTest {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = ObjectMapperFactory.getOrCreateObjectMapper();

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
    }

    @Test
    void whenSignUpNewUser_succeed() throws Exception {
        RegisterUserDto dto = new RegisterUserDto("arman", "pass", "arman yekekhani");

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        User user = userRepository.findByUsername("arman");
        assertNotNull(user);
        assertEquals("arman", user.getUsername());
        assertEquals("arman yekekhani", user.getDisplayName());
    }

    @Test
    void whenSignInExistingUser_shouldSucceed() throws Exception {
        LoginUserDto dto = new LoginUserDto("arman", "pass");
        User user = new User(UUID.randomUUID(),
                "arman",
                passwordEncoder.encode("pass"),
                "arman Y");
        userRepository.save(user);

        mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.expiresIn").exists());
    }
}
