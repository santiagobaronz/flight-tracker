package com.espectrosoft.flightTracker.application.exception;

import com.espectrosoft.flightTracker.application.exception.types.AcademyInactiveException;
import com.espectrosoft.flightTracker.application.exception.types.BusinessException;
import com.espectrosoft.flightTracker.application.exception.types.ModuleDisabledException;
import com.espectrosoft.flightTracker.application.exception.types.NotFoundException;
import com.espectrosoft.flightTracker.application.exception.types.UserInactiveException;
import com.espectrosoft.flightTracker.application.exception.types.ConflictException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String STATUS = "status";
    private static final String MESSAGE = "message";
    private static final String ERRORS = "errors";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.put(fe.getField(), fe.getDefaultMessage());
        }
        body.put(STATUS, HttpStatus.BAD_REQUEST.value());
        body.put(ERRORS, errors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.NOT_FOUND.value());
        body.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(BusinessException ex) {
        log.warn("Business error: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(ConflictException ex) {
        log.warn("Conflict: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.CONFLICT.value());
        body.put(MESSAGE, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({BadCredentialsException.class, UsernameNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleBadCredentials(RuntimeException ex) {
        log.warn("Authentication failed: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.UNAUTHORIZED.value());
        body.put(MESSAGE, "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuth(AuthenticationException ex) {
        log.warn("Authentication exception: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.UNAUTHORIZED.value());
        body.put(MESSAGE, "Authentication failed");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Map<String, Object>> handleAccessDenied(Exception ex) {
        log.warn("Access denied: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.FORBIDDEN.value());
        body.put(MESSAGE, "Access denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(ModuleDisabledException.class)
    public ResponseEntity<Map<String, Object>> handleModuleDisabled(ModuleDisabledException ex) {
        log.warn("Module disabled: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.FORBIDDEN.value());
        body.put(MESSAGE, "Module or content is not accessible");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(AcademyInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleAcademyInactive(AcademyInactiveException ex) {
        log.warn("Academy inactive: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.FORBIDDEN.value());
        body.put(MESSAGE, "Academy is inactive");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(UserInactiveException.class)
    public ResponseEntity<Map<String, Object>> handleUserInactive(UserInactiveException ex) {
        log.warn("User inactive: {}", ex.getMessage());
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.FORBIDDEN.value());
        body.put(MESSAGE, "User is inactive");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception ex) {
        log.error("Unexpected error", ex);
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put(MESSAGE, "Unexpected error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
