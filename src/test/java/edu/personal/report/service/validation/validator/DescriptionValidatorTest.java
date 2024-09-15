package edu.personal.report.service.validation.validator;

import edu.personal.report.model.Report;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class DescriptionValidatorTest {

    DescriptionValidator descriptionValidator = new DescriptionValidator();

    @Test
    void givenValidTitle_whenValidate_shouldReturnNull() {
        Report report = Report.builder().description("valid").build();
        String error = descriptionValidator.validate(report);
        assertNull(error);
    }

    @Test
    void givenInvalidTitle_whenValidate_shouldReturnError() {
        Report report = Report.builder().description("   ").build();
        String error = descriptionValidator.validate(report);
        assertEquals("Description can not be empty!", error);
    }
}