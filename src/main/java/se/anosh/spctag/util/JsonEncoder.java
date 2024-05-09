package se.anosh.spctag.util;

import java.util.Objects;

public final class JsonEncoder {

    private JsonEncoder() {
        throw new AssertionError("Cannot be instantiated");
    }

    private static final int UPPERCASE_BITMASK = ~(1 << 5);
    private static final int LOWERCASE_BITMASK = 1 << 5;

    public static String toJson(Object key, Object value) {
        Objects.requireNonNull(key, "key cannot be null");
        if (value == null) {
            return null; // skip properties where value is null
        }
        return "\"%s\" : %s".formatted(toCamelCase(key.toString()), parseValue(value));
    }

    // Does not work if text is already camel case!
    private static String toCamelCase(String text) {
        StringBuilder sb = new StringBuilder();

        boolean uppercase = false;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (ch == ' ') {
                uppercase = true;
                continue;
            }
            if (ch > ' ' && ch < 'A') { // remove single quotes ' and parentheses
                continue;
            }
            ch = (char) (ch | LOWERCASE_BITMASK);
            if (uppercase) {
                ch = (char) (ch & UPPERCASE_BITMASK);
                uppercase = false;
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    private static String escapeJson(String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);

            String result = switch (ch) {
                case '"' -> "\\\"";
                case '\\' -> "\\\\";
                case '/' -> "\\/";
                case '\b' -> "\\b";
                case '\f' -> "\\f";
                case '\n' -> "\\n";
                case '\r' -> "\\r";
                case '\t' -> "\\t";
                default -> null;
            };

            if (result == null) {
                if (ch >= 0x0 && ch <= 0x1F) {
                    // UTF-16 escaping as per the spec,
                    // all other (multibyte)characters are encoded as UTF-8
                    result = "\\u%04x".formatted((int) ch);
                } else {
                    result = Character.toString(ch);
                }
            }
            sb.append(result);
        }
        return sb.toString();
    }

    private static String parseValue(Object val) {
        boolean quotes = useQuotes(val);
        return quotes
                ? String.format("\"%s\"", escapeJson(val.toString()))
                : Objects.toString(val);
    }

    /*
     * Arrays, objects and NULL are not supported,
     * only Strings, Number, boolean as per JSON specification (subset of JSON)
     *
     */
    private static boolean useQuotes(Object val) {
        Objects.requireNonNull(val, "null not supported");
        if (val instanceof Boolean) {
            throw new IllegalArgumentException("boolean not supported");
        }
        return !(val instanceof Number);
    }

}
