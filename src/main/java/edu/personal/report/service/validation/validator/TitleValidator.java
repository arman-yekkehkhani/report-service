package edu.personal.report.service.validation.validator;

import edu.personal.report.model.Report;
import org.springframework.stereotype.Component;

@Component
public class TitleValidator implements ReportValidator{
    @Override
    public String validate(Report report) {
        if (report.getTitle()== null || report.getTitle().isBlank()) {
            return "Title cannot be empty!";
        }
        return null;
    }
}
