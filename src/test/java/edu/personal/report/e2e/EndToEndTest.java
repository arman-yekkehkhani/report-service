package edu.personal.report.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.personal.report.controller.auth.LoginResponse;
import edu.personal.report.controller.auth.LoginUserDto;
import edu.personal.report.controller.auth.RegisterUserDto;
import edu.personal.report.controller.report.ReportDto;
import edu.personal.report.repository.UserRepository;
import edu.personal.report.util.ObjectMapperFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class EndToEndTest {
    @Autowired
    UserRepository userRepository;

    @Autowired
    MockMvc mockMvc;

    ObjectMapper objectMapper = ObjectMapperFactory.getOrCreateObjectMapper();

    @Test
    void authenticateAndCreateReport() throws Exception {
        userRepository.deleteAll();

        RegisterUserDto signupDto = new RegisterUserDto("arman", "pass", "arman yekekhani");
        mockMvc.perform(MockMvcRequestBuilders.post("/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        LoginUserDto loginUserDto = new LoginUserDto("arman", "pass");
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginUserDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        LoginResponse loginResponse = objectMapper.readValue(result.getResponse().getContentAsString(), LoginResponse.class);

        ReportDto reportDto = new ReportDto(null, "title", "desc");
        mockMvc.perform(MockMvcRequestBuilders.post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reportDto))
                        .header("Authorization", "Bearer " + loginResponse.token())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }
}
