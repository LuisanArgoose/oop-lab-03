package lab03.exception;

/**
 * Файл словаря не удалось прочитать: не существует, нет доступа и т. п.
 */
public class FileReadException extends Exception {

    public FileReadException(String message) {
        super(message);
    }

    public FileReadException(String message, Throwable cause) {
        super(message, cause);
    }
}
