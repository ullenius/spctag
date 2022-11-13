package se.anosh.spctag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    public void setup() {
        uut = new Id666();
    }

    @Test
    public void storeDateAsStringWithoutSeparators() {
        final String expected = "1999/04/01";
        var date = LocalDate.of(1999, Month.APRIL, 1);
        uut.setDateDumpWasCreated(date);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void preSpcFormatDumpedDatesWork() {
        uut.setBinaryTagFormat(Boolean.TRUE);
        final String expected = "1997/05/08";
        var earlyDate = LocalDate.of(1997, Month.MAY, 8);
        uut.setDateDumpWasCreated(earlyDate);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void textualDumpDateFormat() {
        uut.setBinaryTagFormat(Boolean.TRUE);
        final String expected = "1998/12/31";
        uut.setDateDumpWasCreated(NEW_YEARS_EVE_1998);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void binaryDumpDateFormat() {
        uut.setBinaryTagFormat(Boolean.FALSE);
        final String expected = "1998/12/31";
        uut.setDateDumpWasCreated(NEW_YEARS_EVE_1998);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void specFormattedDatesWork() {
        final String mmddyyyy = "05/01/1999";
        final String expected = "1999/05/01";
        uut.setDateDumpWasCreated(mmddyyyy);
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void mmddyyyyDateformatAllowed() {
        final String date = "12/31/2003";
        uut.setDateDumpWasCreated(date);
        final String expected = "2003/12/31";
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void canParseDatesWithDashes() {
        final String date = "12-31-2005";
        uut.setDateDumpWasCreated(date);
        final String expected = "2005/12/31";
        assertEquals(expected, uut.getDateDumpWasCreated());
    }

    @Test
    public void defaultIsTextTags() {
        assertEquals(Boolean.FALSE, uut.isBinaryTagFormat());
    }

    @Test
    public void binaryTagFormatWorks() {
        uut.setBinaryTagFormat(Boolean.TRUE);
        assertEquals(Boolean.TRUE, uut.isBinaryTagFormat());
    }

    @Test
    public void hashCodeAndEqualsIdentical() throws IOException {
        SpcFile textSpc = new SpcFile(TEXT_SPC);
        SpcFile textSpc2 = new SpcFile(TEXT_SPC);

        assertNotSame(textSpc, textSpc2);
        final Id666 first = textSpc.read();
        final Id666 second = textSpc2.read();
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first, second);
    }

    @Test
    public void differentHashcodes() throws IOException {
        SpcFile binary = new SpcFile(BINARY_SPC);
        Id666 binaryTags = binary.read();

        assertNotSame(binaryTags, uut);
        assertNotEquals(binaryTags, uut);
        assertNotEquals(binaryTags.hashCode(), uut.hashCode());
    }

    @Test
    public void nonEqualObjects() throws IOException {
        SpcFile binarySpc = new SpcFile(BINARY_SPC);
        SpcFile textSpc = new SpcFile(TEXT_SPC);

        Id666 binaryTags = binarySpc.read();
        Id666 textTags = textSpc.read();
        assertNotEquals(binaryTags, textTags);
    }

    @Test
    public void sortingWorks() throws IOException {
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
    public void comparatorAllowsNullValues() throws IOException {
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



}
