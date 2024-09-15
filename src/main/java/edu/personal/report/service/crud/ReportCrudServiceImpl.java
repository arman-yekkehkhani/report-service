package edu.personal.report.service.crud;

import edu.personal.report.exception.AuthorizationException;
import edu.personal.report.exception.EntityNotFoundException;
import edu.personal.report.exception.ValidationException;
import edu.personal.report.model.Report;
import edu.personal.report.model.User;
import edu.personal.report.repository.ReportRepository;
import edu.personal.report.service.auth.AuthService;
import edu.personal.report.service.validation.ReportValidatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ReportCrudServiceImpl implements ReportCrudService {
    private static final Logger logger = LoggerFactory.getLogger(ReportCrudServiceImpl.class);

    final ReportRepository reportRepository;
    final AuthService authService;
    final ReportValidatorService reportValidatorService;

    public ReportCrudServiceImpl(ReportRepository reportRepository,
                                 AuthService authService,
                                 ReportValidatorService reportValidatorService) {
        this.reportRepository = reportRepository;
        this.authService = authService;
        this.reportValidatorService = reportValidatorService;
    }

    @Override
    public Report getByUuid(UUID uuid) throws EntityNotFoundException {
        logger.debug("Entering getByUuid with UUID: {}", uuid);
        if (uuid == null) {
            logger.warn("UUID is null");
            throw new IllegalArgumentException("Uuid must not be null!");
        }

        Report report = reportRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    logger.info("No report found with UUID: {}", uuid);
                    return new EntityNotFoundException("No report found with uuid: " + uuid);
                });

        if (!authService.isCurrentUserAuthorized(report.getReporter().getUuid())) {
            logger.warn("Current user {} cannot access report with UUID: {}", authService.getCurrentUser().getUuid() , uuid);
            throw new AuthorizationException("Current user is not authorized to get the post with uuid: " + uuid);
        }
        logger.debug("Exiting getByUuid with report: {}", report);
        return report;
    }

    @Override
    public UUID create(Report report) throws ValidationException {
        logger.debug("Entering create with report: {}", report);
        reportValidatorService.validate(report);
        User user = authService.getCurrentUser();
        if (user == null) {
            logger.error("Current user cannot be null");
            throw new IllegalStateException("Current user cannot be null");
        }

        report.setReporter(user);
        report.setUuid(UUID.randomUUID());

        Report saved = reportRepository.save(report);
        logger.debug("Exiting create with UUID: {}", saved.getUuid());
        return saved.getUuid();
    }

    @Override
    public UUID update(UUID uuid, Report report) throws ValidationException, AuthorizationException, EntityNotFoundException {
        logger.debug("Entering update with UUID: {} and report: {}", uuid, report);

        reportValidatorService.validate(report);

        Report oldReport = reportRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    logger.warn("No report found with UUID: {}", uuid);
                    return new EntityNotFoundException("No report found with uuid: " + uuid);
                });

        if (!authService.isCurrentUserAuthorized(oldReport.getReporter().getUuid())) {
            logger.warn("Current user {} cannot update report with UUID: {}", authService.getCurrentUser().getUuid() , uuid);
            throw new AuthorizationException("Current user is not authorized to update the post with uuid: " + uuid);
        }

        oldReport.setTitle(report.getTitle());
        oldReport.setDescription(report.getDescription());

        Report saved = reportRepository.save(oldReport);
        logger.debug("Exiting update with UUID: {}", saved.getUuid());
        return saved.getUuid();
    }

    @Override
    public void deleteByUuid(UUID uuid) throws AuthorizationException {
        logger.debug("Entering deleteByUuid with UUID: {}", uuid);

        Report report = reportRepository.findByUuid(uuid)
                .orElseThrow(() -> {
                    logger.warn("No report found with UUID: {}", uuid);
                    return new EntityNotFoundException("No report found with uuid: " + uuid);
                });

        if (!authService.isCurrentUserAuthorized(report.getReporter().getUuid())) {
            logger.warn("Current user {} cannot delete report with UUID: {}", authService.getCurrentUser().getUuid() , uuid);
            throw new AuthorizationException("Current user is not authorized to delete the post with uuid: " + uuid);
        }

        reportRepository.delete(report);
    }
}
