package edu.personal.report.service.validation.validator;

import edu.personal.report.model.Report;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TitleValidatorTest {

    TitleValidator titleValidator = new TitleValidator();

    @Test
    void givenValidTitle_whenValidate_shouldReturnNull() {
        Report report = Report.builder().title("title valid").build();
        String error = titleValidator.validate(report);
        assertNull(error);
    }

    @Test
    void givenInvalidTitle_whenValidate_shouldReturnError() {
        Report report = Report.builder().title("   ").build();
        String error = titleValidator.validate(report);
        assertEquals("Title cannot be empty!", error);
    }
}