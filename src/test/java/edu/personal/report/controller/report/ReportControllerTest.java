package edu.personal.report.controller.report;

import edu.personal.report.model.Report;
import edu.personal.report.service.crud.ReportCrudService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    @Mock
    ReportCrudService service;

    @InjectMocks
    ReportController controller;

    @Test
    void testGetByUuid_successful() {
        //given
        UUID uuid = UUID.randomUUID();
        String title = "title";
        String description = "description";
        ReportDto reportDto = new ReportDto(uuid, title, description);
        when(service.getByUuid(uuid)).thenReturn(
                Report.builder().uuid(uuid).title(title).description(description).build()
        );

        //when
        ResponseEntity<ReportDto> response = controller.getByUuid(uuid);

        //then
        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(reportDto, response.getBody())
        );
    }

    @Test
    void testCreateReport_success() {
        //given
        UUID uuid = UUID.randomUUID();
        ReportDto reportDto = new ReportDto(uuid, "title", "description");
        Report report = reportDto.toModel();
        when(service.create(report)).thenReturn(uuid);

        //when
        ResponseEntity<UUID> response = controller.createReport(reportDto);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uuid, response.getBody());
        verify(service).create(report);
    }

    @Test
    void testUpdateReport_success() {
        //given
        UUID uuid = UUID.randomUUID();
        ReportDto reportDto = new ReportDto(uuid, "title", "description");
        Report report = reportDto.toModel();
        report.setUuid(uuid);
        when(service.update(uuid, report)).thenReturn(uuid);


        //when

        ResponseEntity<UUID> response = controller.updateReport(uuid, reportDto);

        //then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uuid, response.getBody());
        verify(service).update(uuid, report);
    }

    @Test
    void testDeleteByUuid_success() {
        //given
        UUID uuid = UUID.randomUUID();
        doNothing().when(service).deleteByUuid(uuid);

        //when
        ResponseEntity<Boolean> response = controller.deleteByUuid(uuid);

        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody());
        verify(service).deleteByUuid(uuid);
    }


}