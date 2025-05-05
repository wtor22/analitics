package quartztop.analitics.exceptions;

import org.springframework.http.HttpStatus;

public class EntityAlreadyExistsException extends AppException{

    public EntityAlreadyExistsException(String entityName, String value) {
        super(entityName + " c таким именем уже существует " + value, HttpStatus.CONFLICT );
    }
}
