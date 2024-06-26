package se.anosh.spctag;


import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.anosh.spctag.dao.*;
import se.anosh.spctag.domain.Id666;

import static org.junit.jupiter.api.Assertions.*;

public class TestModelWithData {

    private static final String ROOT_PATH = "src/test/resources/spc/";

    static final String BINARY_SPC = ROOT_PATH + "binary.spc";

    private static final String BINARY_SPC_WITHOUT_DUMPED_DATE = ROOT_PATH + "binary-nodate.spc";

    private static final String LATIN1_TEXT_SPC = ROOT_PATH + "latin1.spc";
    private static final String UTF8_TEXT_SPC = ROOT_PATH + "utf8.spc";

    static final String TEXT_SPC = ROOT_PATH + "text.spc";

    private static final String SPC_WITH_NO_ID666_TAGS = ROOT_PATH + "containsNoTagSetToTrue.spc";

    private SpcFile spcFile;
    private Id666 id666;

    @BeforeEach
    void setup() throws IOException {
        spcFile = new SpcFile(TEXT_SPC);
        id666 = spcFile.read();
    }

    @Test
    void headerContainsTags() {
        assertTrue(id666.hasId666Tags());
    }

    @Test
    void headerDoesNotContainsTags() throws IOException {
        spcFile = new SpcFile(SPC_WITH_NO_ID666_TAGS);
        id666 = spcFile.read();
        assertFalse(id666.hasId666Tags());
    }

    @Test
    void hasTextTags() {
        assertTrue(id666.isTextTagFormat());
    }

    @Test
    void tagsAreDetectedAsBinary() {
        assertFalse(id666.isBinaryTagFormat());
    }

    @Test
    void dumpedDateBinaryParsing() throws IOException {
        final Id666 id666 = new SpcFile(BINARY_SPC).read();
        assertEquals(Boolean.TRUE, id666.isBinaryTagFormat()); // sanity check
        final var expected = LocalDate.of(1999, Month.DECEMBER, 31);
        assertEquals(expected, id666.getDateDumpWasCreated());
        assertEquals("1999/12/31", id666.dateDumpWasCreated());
    }

    @Test
    void binarySpcMissingDumpDate() throws IOException {
        final Id666 id666 = new SpcFile(BINARY_SPC_WITHOUT_DUMPED_DATE).read();
        assertEquals(Boolean.TRUE, id666.isBinaryTagFormat());
        assertEquals("", id666.dateDumpWasCreated());
        assertNull(id666.getDateDumpWasCreated());
    }

    @Test
    void lengthSecondsText() {
        final int expected = 99;
        assertEquals(expected, id666.getLengthSeconds());
    }

    @Test
    void lengthSecondsBinary() throws IOException {
        final Id666 id666 = new SpcFile(BINARY_SPC).read();
        final int expected = 0xFFAA91; // 24-bit unsigned
        assertEquals(expected, id666.getLengthSeconds());
    }

    @Test
    void fadeLengthText() {
        final int expected = 12345;
        assertEquals(expected, id666.getFadeLengthMilliseconds());
    }

    @Test
    void fadeLengthBinary() throws IOException {
        final Id666 id666 = new SpcFile(BINARY_SPC).read();
        final long expected = 0xFFAA1B58L;
        assertEquals(expected, id666.getFadeLengthMilliseconds());
    }

    @Test
    void latin1FallbackParsing() throws IOException {
        spcFile = new SpcFile(LATIN1_TEXT_SPC);
        Id666 id666 = spcFile.read();

        String expected = "ÅÄÖ åäö ±±±±±±±±";
        assertEquals(expected, id666.getNameOfDumper());
    }

    @Test
    void utf8Parsing() throws IOException {
        spcFile = new SpcFile(UTF8_TEXT_SPC);
        Id666 id666 = spcFile.read();

        String expected = "ÅÄÖ€€€";
        assertEquals(expected, id666.getNameOfDumper());
    }


}
