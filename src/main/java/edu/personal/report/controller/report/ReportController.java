package edu.personal.report.controller.report;

import edu.personal.report.model.Report;
import edu.personal.report.service.crud.ReportCrudService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@SecurityRequirement(name = "Authorization")
@RestController("/api")
public class ReportController {
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);


    final ReportCrudService reportService;

    public ReportController(ReportCrudService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/reports/{uuid}")
    ResponseEntity<ReportDto> getByUuid(@PathVariable UUID uuid) {
        logger.debug("Entering getByUuid with UUID: {}", uuid);
        Report report = reportService.getByUuid(uuid);
        logger.debug("Exiting getByUuid with report: {}", report);
        return new ResponseEntity<>(ReportDto.fromModel(report), HttpStatus.OK);
    }

    @PostMapping("/reports")
    ResponseEntity<UUID> createReport(@RequestBody ReportDto reportDto) {
        logger.debug("Entering createReport with reportDto: {}", reportDto);
        Report report = reportDto.toModel();
        UUID uuid = reportService.create(report);
        logger.debug("Exiting createReport with UUID: {}", uuid);
        return new ResponseEntity<>(uuid, HttpStatus.OK);
    }

    @PostMapping("/reports/{uuid}")
    ResponseEntity<UUID> updateReport(@PathVariable UUID uuid, @RequestBody ReportDto reportDto) {
        logger.debug("Entering updateReport with UUID: {} and reportDto: {}", uuid, reportDto);
        Report report = reportDto.toModel();
        report.setUuid(uuid);
        UUID result = reportService.update(uuid, report);
        logger.debug("Exiting updateReport with result UUID: {}", result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/reports/{uuid}")
    ResponseEntity<Boolean> deleteByUuid(@PathVariable UUID uuid) {
        logger.debug("Entering deleteByUuid with UUID: {}", uuid);
        reportService.deleteByUuid(uuid);
        logger.debug("Exiting deleteByUuid with success");
        return new ResponseEntity<>(true, HttpStatus.OK);
    }
}
