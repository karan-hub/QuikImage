package com.image.quickimage.image.exception;

import com.image.quickimage.image.domain.Response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return new ResponseEntity<>(error, status);
    }


    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleImageNotFound(ImageNotFoundException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, e.getMessage(), request);
    }

    @ExceptionHandler({InvalidDimensionException.class, DuplicateNameException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage(), request);
    }

    @ExceptionHandler(UnsupportedMediaException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMedia(UnsupportedMediaException e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.UNSUPPORTED_MEDIA_TYPE, e.getMessage(), request);
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException e, HttpServletRequest request) {
        // We log the actual error for the dev, but return a clean message to the user
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "File processing failed: " + e.getMessage(), request);
    }

    // MISSING: Handle validation errors (e.g., @Valid or @NotBlank failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e, HttpServletRequest request) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // MISSING: The "Catch-All" for any other unexpected errors
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", request);
    }

}

