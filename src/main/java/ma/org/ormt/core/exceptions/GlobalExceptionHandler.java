package ma.org.ormt.core.exceptions;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.hibernate.StaleObjectStateException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import ma.org.ormt.core.commun.rest.responses.ErrorResponse;
import ma.org.ormt.core.commun.rest.responses.ValidationErrorResponse;
import ma.org.ormt.core.exceptions.handlers.CannotDeleteException;
import ma.org.ormt.core.exceptions.handlers.EntityAlreadyExistsException;
import ma.org.ormt.core.exceptions.handlers.FileUploadException;
import ma.org.ormt.core.exceptions.handlers.ForeignKeyConstraintException;
import ma.org.ormt.core.exceptions.handlers.InvalidDeserializationException;
import ma.org.ormt.core.exceptions.handlers.KeycloakException;
import ma.org.ormt.core.exceptions.handlers.ObjectsValidationException;
import ma.org.ormt.core.exceptions.handlers.ShapefileProcessingException;
import ma.org.ormt.core.exceptions.handlers.ShapefileUploadException;
import ma.org.ormt.core.exceptions.handlers.XMLfileProcessingException;
import ma.org.ormt.core.validators.ObjectsValidator;

@RestControllerAdvice
@RequiredArgsConstructor
@Log4j2
public class GlobalExceptionHandler {

    private final ObjectsValidator<Object> objectsValidator; // Since it's generic, I'm keeping Object here. Adjust as
                                                             // necessary.

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EntityNotFoundException exception) {

        return buildErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), "exception.error.not_found.message",
                "Data");

    }

    @ExceptionHandler(ObjectsValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handleException(ObjectsValidationException exception) {

        ValidationErrorResponse errorResponse = ValidationErrorResponse
                .builder()
                .status(HttpStatus.UNPROCESSABLE_ENTITY.toString())
                .dateTime(LocalDateTime.now())
                .message(exception.getMessage())
                .fields(exception.getErrors())
                .details("exception.error.validation.message")
                .errorType("Validation Data")
                .build();
        log.error("ObjectsValidationException: {}", exception.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errorResponse);

    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException exception) {

        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(),
                "exception.error.validation.message", "Validation Data");

    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalStateException exception) {

        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(),
                "exception.error.validation.message", "Validation Data");

    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {

        return buildErrorResponse(HttpStatus.FORBIDDEN, exception.getMessage(),
                "exception.error.access_denied.message", "permissions");

    }

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEntityAlreadyExistsException(EntityAlreadyExistsException exception) {

        return buildErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY, exception.getMessage(),
                "exception.error.already_exists.message", "Data");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ForeignKeyConstraintException.class)
    public ResponseEntity<ErrorResponse> handleForeignKeyConstraintException(ForeignKeyConstraintException exception) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "exception.error.foreign_key_constraint.message", "ForeignKeyConstraint");

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<ErrorResponse> handleFileUploadException(FileUploadException exception) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "", "File upload");

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CannotDeleteException.class)
    public ResponseEntity<ErrorResponse> handleCannotDeleteException(CannotDeleteException exception) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "exception.error.cannot_delete.message", "delete exception");

    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(StaleObjectStateException.class)
    public ResponseEntity<ErrorResponse> handleStaleObjectStateException(StaleObjectStateException exception) {

        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(),
                "exception.error.stale_object_state.message", "stale object state exception");
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handleObjectOptimisticLockingFailureException(
            ObjectOptimisticLockingFailureException exception) {

        return buildErrorResponse(HttpStatus.CONFLICT, exception.getMessage(),
                "exception.error.optimistic_locking_failure.message", "optimistic locking failure exception");

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ShapefileUploadException.class)
    public ResponseEntity<ErrorResponse> handleShapefileUploadException(ShapefileUploadException exception) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "exception.error.shapefile_upload.message", "Shapefile Upload");

    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<ErrorResponse> keycloakException(KeycloakException exception) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "", "");

    }

    @ExceptionHandler(ShapefileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleShapefileProcessingException(ShapefileProcessingException exception) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "exception.error.shapefile_processing.message", "Shapefile Processing");

    }

    @ExceptionHandler(XMLfileProcessingException.class)
    public ResponseEntity<ErrorResponse> handleXMLProcessingException(XMLfileProcessingException exception) {

        return buildErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(),
                "exception.error.io.message", "XML file Processing");

    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<ErrorResponse> handleIOException(IOException exception) {
        if (isClientDisconnect(exception)) {
            log.warn("Client disconnected before response completed: {}", exception.getMessage());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        }

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage(),
                "exception.error.io.message", "Input/Output Error");

    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        String message = "La taille du fichier dépasse la limite autorisée";
        return buildErrorResponse(HttpStatus.PAYLOAD_TOO_LARGE, message,
                "exception.error.file.too_large", "File upload");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleDeserializationError(HttpMessageNotReadableException ex) {

        // Check for the specific error pattern
        if (isDeserializationErrorForDto(ex)) {
            // Extract the DTO name from the exception message. This may require more
            // sophisticated parsing.

            String dtoName = extractDtoName(ex.getMessage());

            throw new ObjectsValidationException(Map.of(dtoName, Set.of("Invalid data format")));
        }
        return ResponseEntity.badRequest()
                .body(ErrorResponse.builder().message("General deserialization error").build()); // Or any general
                                                                                                 // message
    }

    @ExceptionHandler(InvalidDeserializationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDeserialization(InvalidDeserializationException ex) {

        objectsValidator.validate(ex.getEntityName());

        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "exception.error.invalid_data_format",
                "Data Format");

    }

    private boolean isDeserializationErrorForDto(HttpMessageNotReadableException ex) {
        return ex.getMessage()
                .contains("no String-argument constructor/factory method to deserialize from String value");
    }

    private String extractDtoName(String errorMessage) {
        // This depends on the exact error message format. For simplicity, let's assume
        // DTO names follow the pattern: `package.ClassName`
        // But you may need to adapt this based on actual error messages.
        Pattern pattern = Pattern.compile("of `([a-zA-Z0-9.]+)`");
        Matcher matcher = pattern.matcher(errorMessage);
        if (matcher.find()) {
            return matcher.group(1).substring(matcher.group(1).lastIndexOf('.') + 1);
        }
        return "UnknownEntity";
    }

    private boolean isClientDisconnect(Throwable throwable) {
        Throwable current = throwable;
        while (current != null) {
            String message = current.getMessage();
            String className = current.getClass().getName();
            if ((StringUtils.hasText(message) && (message.contains("Broken pipe")
                    || message.contains("Connection reset by peer")
                    || message.contains("ClientAbortException")))
                    || className.endsWith("ClientAbortException")) {
                return true;
            }
            current = current.getCause();
        }
        return false;
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(HttpStatus status, String exceptionMessage,
            String detailKey, String errorType) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.toString())
                .dateTime(LocalDateTime.now())
                .message(exceptionMessage)
                .details(detailKey)
                .errorType(errorType)
                .build();
        log.error("{}: {}", errorType, exceptionMessage);
        return ResponseEntity.status(status).body(errorResponse);
    }

}
