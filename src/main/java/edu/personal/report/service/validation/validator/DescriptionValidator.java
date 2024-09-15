package edu.personal.report.service.validation.validator;

import edu.personal.report.model.Report;
import org.springframework.stereotype.Component;

@Component
public class DescriptionValidator implements ReportValidator{
    @Override
    public String validate(Report report) {
        if (report.getDescription() == null || report.getDescription().isBlank()) {
            return "Description can not be empty!";
        }
        return null;
    }
}
