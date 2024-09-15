package edu.personal.report.integrationtest;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.personal.report.controller.report.ReportDto;
import edu.personal.report.model.Report;
import edu.personal.report.model.User;
import edu.personal.report.repository.ReportRepository;
import edu.personal.report.repository.UserRepository;
import edu.personal.report.service.auth.AuthService;
import edu.personal.report.util.ObjectMapperFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@WithMockUser(username = "user1", password = "pwd", roles = "USER")
public class ReportControllerTest {
    @MockBean
    AuthService authService;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReportRepository reportRepository;

    @Autowired
    UserRepository userRepository;

    ObjectMapper objectMapper = ObjectMapperFactory.getOrCreateObjectMapper();

    User currentUser;


    @BeforeEach
    void setup() {
        reportRepository.deleteAll();
        currentUser = userRepository.findByUsername("username");
        when(authService.isCurrentUserAuthorized(any())).thenReturn(true);
        when(authService.getCurrentUser()).thenReturn(currentUser);
    }

    @Test
    void whenCreateReport_succeed() throws Exception {
        ReportDto dto = new ReportDto(null, "title", "desc");

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String uuid = result.getResponse().getContentAsString();
        Optional<Report> optionalReport = reportRepository.findByUuid(UUID.fromString(uuid.substring(1, uuid.length() - 1)));
        assertAll(
                () -> assertFalse(optionalReport.isEmpty()),
                () -> assertEquals("title", optionalReport.get().getTitle()),
                () -> assertEquals("desc", optionalReport.get().getDescription()),
                () -> assertEquals(currentUser.getUuid(), optionalReport.get().getReporter().getUuid())
        );
    }

    @Test
    void whenCreateWithInvalidDto_shouldReturnBadRequest() throws Exception {
        Report report = Report.builder().title(" ").description("desc").build();

        mockMvc.perform(MockMvcRequestBuilders.post("/reports")
                        .content(objectMapper.writeValueAsString(report))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid report. Cause: [Title cannot be empty!]"));
    }

    @Test
    void giveReport_whenGetRequest_shouldReturnEntity() throws Exception {
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().uuid(uuid).title("title").description("desc").reporter(currentUser).build();
        reportRepository.save(report);

        mockMvc.perform(MockMvcRequestBuilders.get("/reports/" + uuid).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uuid").value(uuid.toString()))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.description").value("desc"));
    }

    @Test
    void whenGetRequestNonExistingReport_shouldReturn404() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.get("/reports/" + uuid).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No report found with uuid: " + uuid)
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"111", "aaa", "a-a-b-c"})
    void whenGetRequestInvalidUuid_shouldReturnBadRequest(String uuid) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/reports/" + uuid).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenUpdateReport_shouldSucceed() throws Exception {
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().uuid(uuid).title("title").description("desc").reporter(currentUser).build();
        reportRepository.save(report);
        ReportDto dto = new ReportDto(null, "new title", "new description");

        mockMvc.perform(MockMvcRequestBuilders.post("/reports/" + uuid)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("\"" + uuid + "\""));

        Report updatedReport = reportRepository.findByUuid(uuid).orElseThrow();
        assertEquals("new title", updatedReport.getTitle());
        assertEquals("new description", updatedReport.getDescription());
    }

    @Test
    void whenUpdateWithInvalidDto_shouldReturnBadRequest() throws Exception {
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().uuid(uuid).title("title").description("desc").build();
        reportRepository.save(report);
        ReportDto dto = new ReportDto(null, "  ", "new description");

        mockMvc.perform(MockMvcRequestBuilders.post("/reports/" + uuid)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid report. Cause: [Title cannot be empty!]"));

        Report updatedReport = reportRepository.findByUuid(uuid).orElseThrow();
        assertEquals("title", updatedReport.getTitle());
        assertEquals("desc", updatedReport.getDescription());
    }

    @Test
    void whenUpdateNonExistingReport_shouldReturn404() throws Exception {
        UUID uuid = UUID.randomUUID();
        ReportDto dto = new ReportDto(null, "new title", "new description");

        mockMvc.perform(MockMvcRequestBuilders.post("/reports/" + uuid)
                        .content(objectMapper.writeValueAsString(dto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No report found with uuid: " + uuid));
    }

    @Test
    void whenDelete_shouldSucceed() throws Exception {
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().uuid(uuid).title("title").description("desc").reporter(currentUser).build();
        reportRepository.save(report);

        mockMvc.perform(MockMvcRequestBuilders.delete("/reports/" + uuid).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));

        assertTrue(reportRepository.findByUuid(uuid).isEmpty());
    }

    @Test
    void whenDeleteNonExisting_shouldReturn404() throws Exception {
        UUID uuid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.delete("/reports/" + uuid).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No report found with uuid: " + uuid));
    }

}
