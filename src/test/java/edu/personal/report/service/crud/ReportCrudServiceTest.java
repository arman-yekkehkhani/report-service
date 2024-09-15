package edu.personal.report.service.crud;

import edu.personal.report.exception.AuthorizationException;
import edu.personal.report.exception.EntityNotFoundException;
import edu.personal.report.exception.ValidationException;
import edu.personal.report.model.Report;
import edu.personal.report.model.User;
import edu.personal.report.repository.ReportRepository;
import edu.personal.report.service.crud.ReportCrudServiceImpl;
import edu.personal.report.service.auth.AuthService;
import edu.personal.report.service.validation.ReportValidatorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReportCrudServiceTest {
    @Captor
    ArgumentCaptor<Report> reportCaptor;

    @Mock
    ReportValidatorService reportValidatorService;

    @Mock
    ReportRepository reportRepository;

    @Mock
    AuthService authService;

    @InjectMocks
    ReportCrudServiceImpl reportCrudService;

    @Test
    void whenGetByUuid_shouldCallRepo() {
        //given
        when(reportRepository.findByUuid(any())).thenReturn(Optional.of(Report.builder().reporter(new User()).build()));
        when(authService.isCurrentUserAuthorized(any())).thenReturn(true);
        UUID uuid = UUID.randomUUID();

        //when
        reportCrudService.getByUuid(uuid);

        //then
        verify(reportRepository, times(1)).findByUuid(uuid);
    }

    @Test
    void whenGetNonExistingEntity_shouldThrowException() {
        //given
        when(reportRepository.findByUuid(any())).thenReturn(Optional.empty());
        UUID uuid = UUID.randomUUID();

        //when, then
        assertThrows(EntityNotFoundException.class, () -> reportCrudService.getByUuid(uuid));
    }

    @Test
    void whenCreate_shouldSetCurrentUser() throws ValidationException {
        //given
        Report report = Report.builder().title("title").description("description").build();
        when(authService.getCurrentUser()).thenReturn(User.builder().username("user").build());
        when(reportRepository.save(any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        //when
        UUID uuid = reportCrudService.create(report);

        //then
        verify(reportRepository).save(reportCaptor.capture());
        Report actual = reportCaptor.getValue();
        assertAll(
                () -> assertNotNull(actual.getUuid()),
                () -> assertEquals("user", actual.getReporter().getUsername()),
                () -> assertEquals("title", actual.getTitle()),
                () -> assertEquals("description", actual.getDescription()),
                () -> assertEquals(uuid, actual.getUuid())
        );
    }

    @Test
    void whenCreateInvalidReport_shouldThrowException() throws ValidationException {
        //given
        doThrow(new ValidationException("Invalid report")).when(reportValidatorService).validate(any());

        //when,then
        assertThrows(ValidationException.class, () -> reportCrudService.create(Report.builder().build()));
    }

    @Test
    void whenUpdate_shouldSaveAndReturnUuid() throws ValidationException, AuthorizationException {
        //given
        UUID uuid = UUID.randomUUID();
        String newTitle = "new title";
        String newDescription = "new description";
        Report newReport = Report.builder().title(newTitle).description(newDescription).build();
        when(authService.isCurrentUserAuthorized(any())).thenReturn(true);
        when(reportRepository.findByUuid(uuid)).thenReturn(
                Optional.of(Report.builder()
                        .uuid(uuid)
                        .title("old title")
                        .description("old description")
                        .reporter(new User())
                        .build())
        );
        when(reportRepository.save(any())).thenAnswer((invocation) -> invocation.getArguments()[0]);

        //when
        UUID actualUuid = reportCrudService.update(uuid, newReport);

        //then
        verify(reportRepository, times(1)).save(reportCaptor.capture());
        Report repoArg = reportCaptor.getValue();
        assertAll(
                () -> assertEquals(uuid, actualUuid),
                () -> assertEquals(repoArg.getTitle(), newTitle),
                () -> assertEquals(repoArg.getDescription(), newDescription)
        );
    }

    @Test
    void whenUpdateWithNonExistingEntity_shouldThrowException() {
        //given
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().build();
        when(reportRepository.findByUuid(uuid)).thenReturn(Optional.empty());

        //when,then
        assertThrows(EntityNotFoundException.class, () -> reportCrudService.update(uuid, report));
        verify(reportRepository, times(0)).save(any());
    }

    @Test
    void whenUpdateWithUnauthorizedUser_shouldThrowAuthorizationException() {
        //given
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().build();
        when(authService.isCurrentUserAuthorized(any())).thenReturn(false);
        when(authService.getCurrentUser()).thenReturn(User.builder().uuid(UUID.randomUUID()).build());
        when(reportRepository.findByUuid(uuid)).thenReturn(
                Optional.of(Report.builder()
                        .reporter(new User())
                        .build())
        );

        //when, then
        assertThrows(AuthorizationException.class, () -> reportCrudService.update(uuid, report));
        verify(reportRepository, times(0)).save(any(Report.class));
    }

    @Test
    void whenUpdateWithInvalidData_shouldThrowValidationException() throws ValidationException {
        //given
        UUID uuid = UUID.randomUUID();
        Report report = Report.builder().title("   ").build();
        doThrow(new ValidationException("Invalid report")).when(reportValidatorService).validate(any());

        //when, then
        assertThrows(ValidationException.class, () -> reportCrudService.update(uuid, report));
        verify(reportRepository, times(0)).save(any(Report.class));
    }

    @Test
    void whenDeleteByAuthorizedUser_shouldCallRepo() throws AuthorizationException {
        //given
        when(authService.isCurrentUserAuthorized(any())).thenReturn(true);
        UUID uuid = UUID.randomUUID();
        when(reportRepository.findByUuid(any())).thenReturn(Optional.of(Report.builder().reporter(new User()).build()));

        //when
        reportCrudService.deleteByUuid(uuid);

        //then
        verify(reportRepository, times(1)).delete(any());
    }

    @Test
    void whenDeleteByUnauthorizedUser_shouldThrowException() {
        //given
        when(authService.isCurrentUserAuthorized(any())).thenReturn(false);
        when(authService.getCurrentUser()).thenReturn(User.builder().uuid(UUID.randomUUID()).build());
        when(reportRepository.findByUuid(any())).thenReturn(Optional.of(Report.builder().reporter(new User()).build()));

        //when, then
        assertThrows(AuthorizationException.class, () -> reportCrudService.deleteByUuid(UUID.randomUUID()));
        verify(reportRepository, times(0)).delete(any());
    }
}
