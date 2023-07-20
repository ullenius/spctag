package se.anosh.spctag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.tinylog.Logger;
import se.anosh.spctag.dao.SpcDao;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Id666;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static se.anosh.spctag.TestModelWithData.BINARY_SPC;
import static se.anosh.spctag.TestModelWithData.TEXT_SPC;

public class Id666Test {

    private static final LocalDate NEW_YEARS_EVE_1998 =
            LocalDate.of(1998, Month.DECEMBER, 31);

    private Id666 uut;

    @BeforeEach
    void setup() {
        uut = new Id666();
    }

    @ParameterizedTest
    @MethodSource("dateStrings")
    void texttagsDateParsing(final String date, final LocalDate expected) {
        uut.setDateDumpWasCreated(date);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    private static Object[][] dateStrings() { // text tags
        final LocalDate MAY_9_1998 = LocalDate.of(1998, Month.MAY, 9);
        return new Object[][]{
                // input date, expected
                {"05/01/1999", LocalDate.of(1999, Month.MAY, 1)}, // spec format, mm-dd-yyyy
                {"31/12/2003", LocalDate.of(2003, Month.DECEMBER, 31)}, // dd-mm-yyyy, non-standard
                {"12-31-2005", LocalDate.of(2005, Month.DECEMBER, 31)},
                {"2005-11-05", LocalDate.of(2005, Month.NOVEMBER, 5)}, // dashes as separators
                {"1999-31-12", LocalDate.of(1999, Month.DECEMBER, 31)},
                {"5-9-1998", MAY_9_1998}, // leading zeroes are ignored
                {"05-9-1998", MAY_9_1998},
                {"5-09-1998", MAY_9_1998},
                {"1992-02-29", LocalDate.of(1992, Month.FEBRUARY, 29)}, // predates the SPC-format
                {"9999-12-12", LocalDate.of(9999, Month.DECEMBER, 12)}, // max value from spec
                {"2001-02-29", null} // fails parsing, non-leap year
        };
    }

    @Test
    void textualDumpDateFormat() {
        uut.setBinaryTagFormat(Boolean.TRUE);
        final String expected = "1998/12/31";
        uut.setDateDumpWasCreated(NEW_YEARS_EVE_1998);
        assertEquals(expected, uut.dateDumpWasCreated());
    }

    @Test
    void binaryDumpDateFormat() {
        uut.setBinaryTagFormat(Boolean.FALSE);
        final String expected = "1998/12/31";
        uut.setDateDumpWasCreated(NEW_YEARS_EVE_1998);
        assertEquals(expected, uut.dateDumpWasCreated());
    }

    @Test
    void storeDateAsStringWithoutSeparators() {
        final String expected = "1999/04/01";
        var date = LocalDate.of(1999, Month.APRIL, 1);
        uut.setDateDumpWasCreated(date);
        assertEquals(expected, uut.dateDumpWasCreated());
    }

    @Test
    void preSpcFormatDumpedDatesWork() {
        uut.setBinaryTagFormat(Boolean.TRUE);
        final String expected = "1997/05/08";
        var earlyDate = LocalDate.of(1997, Month.MAY, 8);
        uut.setDateDumpWasCreated(earlyDate);
        assertEquals(expected, uut.dateDumpWasCreated());
    }

    @Test
    void defaultIsTextTags() {
        assertEquals(Boolean.FALSE, uut.isBinaryTagFormat());
    }

    @Test
    void binaryTagFormatWorks() {
        uut.setBinaryTagFormat(Boolean.TRUE);
        assertEquals(Boolean.TRUE, uut.isBinaryTagFormat());
    }

    @Test
    void hashCodeAndEqualsIdentical() throws IOException {
        SpcFile textSpc = new SpcFile(TEXT_SPC);
        SpcFile textSpc2 = new SpcFile(TEXT_SPC);

        assertNotSame(textSpc, textSpc2);
        final Id666 first = textSpc.read();
        final Id666 second = textSpc2.read();
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, second);
    }

    @Test
    void differentHashcodes() throws IOException {
        SpcFile binary = new SpcFile(BINARY_SPC);
        Id666 binaryTags = binary.read();

        assertNotSame(binaryTags, uut);
        assertNotEquals(binaryTags, uut);
        assertNotEquals(binaryTags.hashCode(), uut.hashCode());
    }

    @Test
    void nonEqualObjects() throws IOException {
        SpcFile binarySpc = new SpcFile(BINARY_SPC);
        SpcFile textSpc = new SpcFile(TEXT_SPC);

        Id666 binaryTags = binarySpc.read();
        Id666 textTags = textSpc.read();
        assertNotEquals(binaryTags, textTags);
    }

    @Test
    void sortingWorks() throws IOException {
        final SpcFile otherFile = new SpcFile(BINARY_SPC);
        final Id666 other = otherFile.read();

        List<Id666> myList = new LinkedList<>();
        myList.add(other);
        myList.add(uut);
        myList.add(other);
        myList.add(uut);

        myList.sort(null);
        myList.forEach(Logger::debug);
        assertEquals(uut, myList.get(0));
        assertEquals(uut, myList.get(1));
        assertEquals(other, myList.get(2));
        assertEquals(other, myList.get(3));
    }

    @Test
    void comparatorAllowsNullValues() throws IOException {
        final SpcDao binary = new SpcFile(BINARY_SPC);
        final Id666 binaryTags = binary.read();
        final Id666 emptyTags = new Id666();

        List<Id666> myList = new LinkedList<>();
        myList.add(binaryTags);
        myList.add(emptyTags);
        myList.sort(null);

        assertNull(myList.get(0).getSongTitle());
        assertNull(myList.get(0).getGameTitle());
        assertNull(myList.get(0).getArtist());
        assertNotNull(myList.get(1).getGameTitle());
        assertNotNull(myList.get(1).getArtist());
        assertNotNull(myList.get(1).getSongTitle());
    }

    @Test
    void minorVersionUint8() {
        Id666 tag = new Id666();
        tag.setVersion(Short.MAX_VALUE);
        final short expected = 0xFF;
        assertEquals(expected, tag.getVersion());
    }


}
