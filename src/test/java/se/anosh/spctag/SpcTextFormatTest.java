package se.anosh.spctag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static se.anosh.spctag.TestModelWithData.TEXT_SPC;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.emulator.factory.Emulator;

public class SpcTextFormatTest {

    private SpcFile spcFile;
    private Id666 id666;

    private static final String SPC_RANDOM_BYTES = "src/test/resources/spc/randomBytes.spc";


    @BeforeEach
    void setup() throws IOException {
        spcFile = new SpcFile(TEXT_SPC);
        id666 = spcFile.read();
    }

    @Test
    void textualHeaderFormat() {
        assertEquals(Boolean.TRUE, id666.isTextTagFormat());
    }

    @Test
    void fileWithValidHeader() {
        // first 27 of string should equal "SNES-SPC700 Sound File Data"
        String headerWithoutVersionNumber = id666.getHeader().substring(0, 27);
        assertEquals("SNES-SPC700 Sound File Data", headerWithoutVersionNumber); // case sensitive
    }

    @Test
    void invalidHeaderFails() {
        assertThrows(IOException.class, () -> spcFile = new SpcFile(SPC_RANDOM_BYTES));
    }

    @Test
    void parseArtist() {
        final String expected = "Taro Kudou, M. C. Ada";
        assertEquals(expected, id666.getArtist());
    }

    @Test
    void parseGameTitle() {
        final String expected = "Axelay!";
        assertEquals(expected, id666.getGameTitle());
    }

    @Test
    void parseSong() {
        final String expected = "Axelay";
        assertEquals(expected, id666.getSongTitle());
    }

    @Test
    void parseDumper() {
        final String expected = "Datschge";
        assertEquals(expected, id666.getNameOfDumper());
    }

    @Test
    void parseComments() {
        final String expected = "banAna";
        assertEquals(expected, id666.getComments());
    }

    @Test
    void textualDumpDate() {
        final LocalDate expected = LocalDate.of(1999, Month.DECEMBER, 24);
        assertEquals(expected, id666.getDateDumpWasCreated());
    }

    @Test
    void emulatorUsedToCreateDump() {
        final Emulator.Name emulator = Emulator.Name.Unknown;
        assertEquals(emulator, id666.getEmulatorUsedToCreateDump().getName());
    }

}
