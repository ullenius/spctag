package se.anosh.spctag.util;

public class StringUtil {

    public static void printIf(String format, String data) {
        if (data != null) {
            System.out.printf(format, data);
        }
    }

}
