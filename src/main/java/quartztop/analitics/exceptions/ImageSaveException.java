package quartztop.analitics.exceptions;

import org.springframework.http.HttpStatus;

public class ImageSaveException extends AppException {
    public ImageSaveException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}
