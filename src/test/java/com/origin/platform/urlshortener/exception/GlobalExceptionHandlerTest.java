package com.origin.platform.urlshortener.exception;

import com.origin.platform.urlshortener.dto.response.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {
    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleResourceAlreadyExist_returnsBadRequest() {
        ResourceAlreadyExistException ex = new ResourceAlreadyExistException("Already exists");
        ResponseEntity<ErrorResponse> response = handler.handleResourceAlreadyExist(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Already exists", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleResourceNotFound_returnsNotFound() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFound(ex);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }

    @Test
    void handleValidationExceptions_returnsValidationErrors() {
        BindException bindException = new BindException(new Object(), "objectName");
        bindException.addError(new FieldError("objectName", "field1", "must not be blank"));
        bindException.addError(new FieldError("objectName", "field2", "must be positive"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindException.getBindingResult());
        ResponseEntity<ErrorResponse> response = handler.handleValidationExceptions(ex);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", response.getBody().getMessage());
        Map<String, String> errors = response.getBody().getErrors();
        assertNotNull(errors);
        assertEquals("must not be blank", errors.get("field1"));
        assertEquals("must be positive", errors.get("field2"));
    }

    @Test
    void handleGenericException_returnsInternalServerError() {
        Exception ex = new Exception("Something went wrong");
        ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Something went wrong", response.getBody().getMessage());
        assertNull(response.getBody().getErrors());
    }
}
