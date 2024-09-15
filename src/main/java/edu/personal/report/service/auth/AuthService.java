package edu.personal.report.service.auth;

import edu.personal.report.controller.auth.LoginUserDto;
import edu.personal.report.controller.auth.RegisterUserDto;
import edu.personal.report.model.User;

import java.util.UUID;

public interface AuthService {
    User signup(RegisterUserDto dto);
    User authenticate(LoginUserDto dto);
    boolean isCurrentUserAuthorized(UUID reportedUid);
    User getCurrentUser();
}
