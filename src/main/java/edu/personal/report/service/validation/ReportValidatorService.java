package edu.personal.report.service.validation;

import edu.personal.report.exception.ValidationException;
import edu.personal.report.model.Report;

public interface ReportValidatorService {
    void validate(Report report) throws ValidationException;
}
