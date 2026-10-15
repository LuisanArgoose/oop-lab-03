package lab03;

import lab03.exception.FileReadException;
import lab03.exception.InvalidFileFormatException;

import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * Программа-переводчик: читает словарь из файла и переводит текст из консоли.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        Charset consoleCharset = forceConsoleUtf8();

        if (args.length == 0 || args[0].isBlank()) {
            System.out.println("Укажите путь к файлу словаря: java lab03.Main <файл>");
            return;
        }

        Dictionary dictionary;
        try {
            dictionary = Dictionary.load(Path.of(args[0]));
        } catch (FileReadException | InvalidFileFormatException e) {
            System.out.println(e.getMessage());
            return;
        }

        Translator translator = new Translator(dictionary);
        System.out.println("Словарь загружен. Введите текст для перевода (пустая строка — выход).");

        try (Scanner scanner = new Scanner(System.in, consoleCharset)) {
            while (true) {
                System.out.print("> ");
                if (!scanner.hasNextLine()) {
                    break;
                }
                String line = scanner.nextLine();
                if (line.isBlank()) {
                    break;
                }
                System.out.println(translator.translate(line));
            }
        }

        System.out.println("Программа завершена.");
    }

    /**
     * Принудительно переводит {@code System.out} на UTF-8, чтобы кириллица
     * корректно отображалась независимо от кодовой страницы консоли Windows.
     *
     * @return кодировка, в которой нужно читать ввод
     */
    private static Charset forceConsoleUtf8() {
        java.io.Console console = System.console();
        Charset charset = (console != null) ? console.charset() : StandardCharsets.UTF_8;
        try {
            PrintStream utf8 = new PrintStream(
                    new FileOutputStream(FileDescriptor.out), true, charset.name());
            System.setOut(utf8);
        } catch (UnsupportedEncodingException ignored) {
            // Недостижимая ветка: все кодовые страницы консоли поддерживаются.
        }
        return charset;
    }
}
