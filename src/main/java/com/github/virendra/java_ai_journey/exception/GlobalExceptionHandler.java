package com.github.virendra.java_ai_journey.exception;

import com.github.virendra.java_ai_journey.dto.response.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles PDF reading and IO errors
     * @param exeption
     * @param request
     * @return ResponseEntity<ErrorResponseDTO>
     */
    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponseDTO> handleIOException(IOException exeption,
                                                              HttpServletRequest request){
        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCodes.FILE_PROCESSING_ERROR,
                ErrorCodes.FILE_PROCESSING_ERROR_MESSAGE + exeption.getMessage(),
                request.getRequestURI());
    }

    /**
     * Handles when uploaded file exceeds 10MB limit
     * @param exeption
     * @param request
     * @return ResponseEntity<ErrorResponseDTO>
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponseDTO> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exeption,
                                                                        HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodes.FILE_TOO_LARGE,
                ErrorCodes.FILE_TOO_LARGE_MESSAGE,
                request.getRequestURI());
    }

    /**
     * Handles missing required request parameters
     * @param exception
     * @param request
     * @return ResponseEntity<ErrorResponseDTO>
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponseDTO> handleMissingParams(MissingServletRequestParameterException exception,
                                                                HttpServletRequest request){
        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodes.MISSING_PARAMETER,
                ErrorCodes.MISSING_PARAMETER_MESSAGE + exception.getParameterName(),
                request.getRequestURI());
    }

    /**
     * Handles @RequestParam validation errors
     * thrown when @Validated is used on controller class
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request) {

        // Get the first violation message
        String errorMessage = exception.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("Validation failed");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodes.VALIDATION_ERROR,
                errorMessage,
                request.getRequestURI());
    }

    /**
     * Handles @Valid DTO validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationErrors(MethodArgumentNotValidException exception,
                                                                   HttpServletRequest request) {

        String errorMessage = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField()
                        + " — " + error.getDefaultMessage())
                .orElse("Validation failed");

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodes.VALIDATION_ERROR,
                errorMessage,
                request.getRequestURI());
    }

    /**
     * Handles illegal argument errors
     * @param exception
     * @param request
     * @return ResponseEntity<ErrorResponseDTO>
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(IllegalArgumentException exception,
                                                                  HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ErrorCodes.INVALID_INPUT,
                exception.getMessage(),
                request.getRequestURI());
    }

    /**
     * Catches ALL other unexpected exceptions not handled above.
     * @param exception
     * @param request
     * @return ResponseEntity<ErrorResponseDTO>
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGenericException(Exception exception,
                                                                   HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ErrorCodes.INTERNAL_SERVER_ERROR,
                ErrorCodes.INTERNAL_SERVER_ERROR_MESSAGE + exception.getMessage(),
                request.getRequestURI());
    }

    /**
     * Helper method to build consistent error responses
     */
    private ResponseEntity<ErrorResponseDTO> buildErrorResponse(
            HttpStatus status,
            String error,
            String errorMessage,
            String path) {

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .status(status.value())
                .error(error)
                .errorMessage(errorMessage)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }
}
