package lab03.exception;

/**
 * Формат строки словаря не соответствует шаблону {@code слово или выражение | перевод}.
 */
public class InvalidFileFormatException extends Exception {

    public InvalidFileFormatException(String message) {
        super(message);
    }
}
