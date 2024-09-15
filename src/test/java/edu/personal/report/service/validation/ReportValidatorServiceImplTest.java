package edu.personal.report.service.validation;

import edu.personal.report.exception.ValidationException;
import edu.personal.report.model.Report;
import edu.personal.report.service.validation.validator.ReportValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportValidatorServiceImplTest {
    @Mock
    ReportValidator validator1;

    @Mock
    ReportValidator validator2;

    ReportValidatorServiceImpl validatorService;

    @BeforeEach
    void init() {
        validatorService = new ReportValidatorServiceImpl(List.of(validator1, validator2));
    }

    @Test
    void whenValidate_shouldCallAllValidators() throws ValidationException {
        //given
        Report report = Report.builder().build();

        //when
        validatorService.validate(report);

        //then
        verify(validator1, times(1)).validate(report);
        verify(validator2, times(1)).validate(report);
    }

    @Test
    void whenValidateInvalidReportShouldTrowException() {
        //given
        Report report = Report.builder().build();
        when(validator1.validate(report)).thenReturn("error 1");
        when(validator2.validate(report)).thenReturn("error 2");

        //when
        ValidationException exception = assertThrows(ValidationException.class, () -> validatorService.validate(report));
        assertEquals("Invalid report. Cause: [error 1, error 2]", exception.getMessage());
    }

}