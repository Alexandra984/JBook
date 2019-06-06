package dao.exception;

public class NotFoundException extends Exception {
    @Override
    public String getMessage() {
        return "Entity not found";
    }
}
