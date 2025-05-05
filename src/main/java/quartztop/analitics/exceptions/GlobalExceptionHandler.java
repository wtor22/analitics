package quartztop.analitics.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiErrorResponse> handleAppException(AppException ex, HttpServletRequest request) {
        log.warn("AppException: {} - {}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(
                buildResponse(ex.getClass().getSimpleName(), ex.getMessage(), ex.getStatus(), request.getRequestURI()),
                ex.getStatus()
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiErrorResponse> handleMaxUploadSizeException(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("File too large: {}", ex.getMessage());

        return new ResponseEntity<>(
                buildResponse("FileTooLarge", "Файл слишком большой. Максимальный размер — 1MB.", HttpStatus.BAD_REQUEST, request.getRequestURI()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleGeneralException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        return new ResponseEntity<>(
                buildResponse("InternalServerError", "Что-то пошло не так. Мы уже чиним 🙃", HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI()),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ApiErrorResponse buildResponse(String error, String message, HttpStatus status, String path) {
        return ApiErrorResponse.builder()
                .error(error)
                .message(message)
                .status(status.value())
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
