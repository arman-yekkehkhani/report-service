package edu.personal.report.service.crud;


import edu.personal.report.exception.AuthorizationException;
import edu.personal.report.exception.EntityNotFoundException;
import edu.personal.report.exception.ValidationException;
import edu.personal.report.model.Report;

import java.util.UUID;

public interface ReportCrudService {
    Report getByUuid(UUID uuid) throws EntityNotFoundException;

    UUID create(Report report) throws ValidationException;

    UUID update(UUID uuid, Report report) throws ValidationException, AuthorizationException, EntityNotFoundException;

    void deleteByUuid(UUID uuid) throws AuthorizationException;
}
