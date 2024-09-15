package edu.personal.report.service.validation;

import edu.personal.report.exception.ValidationException;
import edu.personal.report.model.Report;
import edu.personal.report.service.validation.validator.ReportValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class ReportValidatorServiceImpl implements ReportValidatorService {
    private static final Logger logger = LoggerFactory.getLogger(ReportValidatorServiceImpl.class);

    final List<ReportValidator> validators;

    public ReportValidatorServiceImpl(List<ReportValidator> validators) {
        this.validators = validators;
    }

    @Override
    public void validate(Report report) throws ValidationException {
        logger.debug("Entering validate with report: {}", report);
        List<String> errors = validators.stream().map(v -> v.validate(report)).filter(Objects::nonNull).toList();
        if (!errors.isEmpty()) {
            logger.info("Validation failed for report with errors: {}", errors);
            throw new ValidationException("Invalid report. Cause: " + errors);
        }
        logger.debug("Validation successful for report: {}", report);
    }
}
