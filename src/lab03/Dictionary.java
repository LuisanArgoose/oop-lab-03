package lab03;

import lab03.exception.FileReadException;
import lab03.exception.InvalidFileFormatException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Словарь «фраза | перевод». Поиск не зависит от регистра и предпочитает
 * самую длинную подходящую левую часть.
 */
public class Dictionary {

    private final List<Entry> entries;

    private Dictionary(List<Entry> entries) {
        this.entries = entries;
    }

    /**
     * Читает словарь из файла. Каждая непустая строка должна иметь вид
     * {@code слово или выражение | перевод}.
     */
    public static Dictionary load(Path path) throws FileReadException, InvalidFileFormatException {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FileReadException("Не удалось прочитать файл словаря: " + path, e);
        }

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.isEmpty()) {
                continue;
            }
            entries.add(parseLine(line, i + 1));
        }

        entries.sort(Comparator.comparingInt((Entry e) -> e.source.length()).reversed());
        return new Dictionary(entries);
    }

    private static Entry parseLine(String line, int lineNumber) throws InvalidFileFormatException {
        int separator = line.indexOf('|');
        if (separator < 0 || line.indexOf('|', separator + 1) >= 0) {
            throw new InvalidFileFormatException(
                    "Строка " + lineNumber + ": ожидается формат \"слово или выражение | перевод\"");
        }

        String source = line.substring(0, separator).trim();
        String translation = line.substring(separator + 1).trim();
        if (source.isEmpty() || translation.isEmpty()) {
            throw new InvalidFileFormatException(
                    "Строка " + lineNumber + ": левая и правая части не могут быть пустыми");
        }

        return new Entry(source, translation);
    }

    /**
     * Самая длинная словарная фраза, которая начинается в позиции {@code index}
     * текста и ограничена границами слов. {@code null}, если совпадения нет.
     */
    public Entry matchAt(String text, int index) {
        for (Entry entry : entries) {
            if (startsWithPhrase(text, index, entry.source)) {
                return entry;
            }
        }
        return null;
    }

    private static boolean startsWithPhrase(String text, int index, String phrase) {
        if (index + phrase.length() > text.length()) {
            return false;
        }
        if (!text.regionMatches(true, index, phrase, 0, phrase.length())) {
            return false;
        }
        return isWordBoundary(text, index - 1) && isWordBoundary(text, index + phrase.length());
    }

    private static boolean isWordBoundary(String text, int index) {
        if (index < 0 || index >= text.length()) {
            return true;
        }
        return !Character.isLetterOrDigit(text.charAt(index));
    }

    public static final class Entry {
        private final String source;
        private final String translation;

        private Entry(String source, String translation) {
            this.source = source;
            this.translation = translation;
        }

        public String getSource() {
            return source;
        }

        public String getTranslation() {
            return translation;
        }
    }
}
