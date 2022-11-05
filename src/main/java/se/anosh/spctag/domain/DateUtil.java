package se.anosh.spctag.domain;

import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

final class DateUtil {

    private DateUtil() {
        throw new AssertionError("Cannot be instantiated");
    }

    private static final DateTimeFormatter DUMP_DATE_FORMAT = DateTimeFormatter
            .ofPattern("uuuuMMdd")
            .withResolverStyle(ResolverStyle.STRICT);

    static DateTimeFormatter xid6DumpedDateFormatter() {
        return DUMP_DATE_FORMAT;
    }

    static DateTimeFormatter id666DumpedDateFormatter() {
        return DUMP_DATE_FORMAT;
    }

}
