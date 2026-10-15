package lab03;

/**
 * Переводит текст по словарю. Регистр не учитывается. Неизвестные слова
 * остаются как есть. При нескольких вариантах берётся самая длинная фраза.
 */
public class Translator {

    private final Dictionary dictionary;

    public Translator(Dictionary dictionary) {
        if (dictionary == null) {
            throw new IllegalArgumentException("Словарь не может быть null.");
        }
        this.dictionary = dictionary;
    }

    public String translate(String text) {
        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < text.length()) {
            if (!Character.isLetterOrDigit(text.charAt(i))) {
                result.append(text.charAt(i));
                i++;
                continue;
            }

            Dictionary.Entry match = dictionary.matchAt(text, i);
            if (match != null) {
                result.append(match.getTranslation());
                i += match.getSource().length();
            } else {
                int end = i;
                while (end < text.length() && Character.isLetterOrDigit(text.charAt(end))) {
                    end++;
                }
                result.append(text, i, end);
                i = end;
            }
        }
        return result.toString();
    }
}
