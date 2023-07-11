package se.anosh.spctag;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.anosh.spctag.dao.SpcDao;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.emulator.factory.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;


public class Xid6Test {

    private Path spc;
    private SpcDao dao;
    private Xid6 uut;

    private static final String SPC_XID6 = "src/test/resources/spc/xid6.spc";

    private static final String SPC_MUTED_CHANNELS_ALL_BITS_TOGGLED = "src/test/resources/spc/muted.spc";

    private static final String SPC_MUTED_CHANNELS_0x43 = "src/test/resources/spc/bar.spc";

    @BeforeEach
    void setup() throws IOException {
        spc = Paths.get(SPC_XID6);
        dao = new SpcFile(spc.toString());
        uut = dao.readXid6();
    }

    @Test
    void dateDumpedNotSet() {
        assertNull(uut.getDate());
    }

    @Test
    void emulatorUsedNotSet() {
        assertEquals(Emulator.Name.Snes9x, uut.getEmulator().getName());
    }

    @Test
    void gameName() {
        assertEquals("Kyouraku Sanyou Toyomaru Okumura Daiichi Maruhon Parlor Parlor! 4 CR", uut.getGame());
    }

    @Test
    void commentsWorks() {
        assertEquals("\"Auld Lang Syne\", scottish folk song. Taken down in musical notation by Robert Burns.",
                uut.getComments());
    }

    @Test
    void publishersNameWorks() {
        assertEquals("Nihon Telenet", uut.getPublisher());
    }

    @Test
    void copyrightYearWorks() {
        assertEquals(Year.of(1995), uut.getYear());
    }

    @Test
    void introLength() {
        assertEquals(Double.valueOf(12.6), uut.getIntrolength());
    }

    @Test
    void fadeLength() {
        assertEquals(Integer.valueOf(448000), uut.getFadeLength());
    }

    @Test
    void dateConversionWorks() {
        final int dumpedDate = 20201224;
        final LocalDate expected = LocalDate.of(2020, Month.DECEMBER, 24);
        final Xid6 uut2 = new Xid6();
        uut2.setDate(dumpedDate);
        assertEquals(expected, uut2.getDate());
    }

    @Test
    void dateConversionReturnsNullOnParsingError() {
        final int erroneousDumpedDate = 991224; // 1999-12-24
        final Xid6 xid6 = new Xid6();
        xid6.setDate(erroneousDumpedDate);
        assertNull(xid6.getDate());
    }

    @Test
    void dateFormatterUsesYear() {
        final int date = 20040404;
        final Xid6 xid6 = new Xid6();
        xid6.setDate(date);
        assertEquals(LocalDate.of(2004, Month.APRIL, 4), xid6.getDate());
    }

    @Test
    void dateRequiresTwoDigitsForMonthAndDay() {
        final int monthOneDigit = 2000110;
        final int dayOneDigit = 2003121;
        final Xid6 month = new Xid6();
        final Xid6 day = new Xid6();

        month.setDate(monthOneDigit);
        day.setDate(dayOneDigit);

        assertNull(month.getDate());
        assertNull(day.getDate());
    }

    @Test
    void mutedChannelHasEightBits() {
        short mutedChannels = 0xFF;
        final Xid6 uut2 = new Xid6();
        uut2.setMutedChannels(mutedChannels);
        final String expected = "11111111";
        assertEquals(expected, uut2.getMutedChannels());
    }

    @Test
    void parseMutedChannelsFromFile() throws IOException {
        Path spc2 = Paths.get(SPC_MUTED_CHANNELS_0x43);
        SpcDao dao2 = new SpcFile(spc2.toString());
        Xid6 uut2 = dao2.readXid6();
        final String bitpattern = "01000011";
        assertEquals(bitpattern, uut2.getMutedChannels());
    }

    @Test
    void parseMutedChannelsFromFileAllBitsSet() throws IOException {
        Path spc2 = Paths.get(SPC_MUTED_CHANNELS_ALL_BITS_TOGGLED);
        SpcDao dao2 = new SpcFile(spc2.toString());
        Xid6 uut2 = dao2.readXid6();
        final String bitpattern = "11111111";
        assertEquals(bitpattern, uut2.getMutedChannels());
    }

    @Test
    void ostTrackConstructorWorks() {
        final byte trackNumber = 42;
        final Xid6.OstTrack ostTrack = new Xid6.OstTrack(trackNumber);
        assertEquals(Integer.toString(trackNumber), ostTrack.toString());
    }

    @Test
    void ostTrackWithTrackCharacter() {
        // OST track (upper byte is the number 0-99, lower byte is an optional ASCII character
        final byte trackNumber = 99;
        final Xid6.OstTrack ostTrack = new Xid6.OstTrack(trackNumber, 'A');
        assertEquals("99 A", ostTrack.toString());
    }

    // ------------ NULL CHECKS -------------------------------------
    @Test
    void mutedVoices() {
        assertFalse(uut.hasMutedChannels());
    }

    @Test
    void songNameNotSet() {
        assertNull(uut.getSong());
    }

    @Test
    void dumpersNameNotSet() {
        assertNull(uut.getDumper());
    }

    @Test
    void artistNotSet() {
        assertNull(uut.getArtist());
    }


    @Test
    void ostTitleNotSet() {
        assertNull(uut.getOstTitle());
    }

    @Test
    void ostDiscNotSet() {
        assertNull(uut.getOstDisc());
    }

    @Test
    void ostTrackNotSet() {
        assertNull(uut.getOstTrack());
    }

    @Test
    void numberOfLoopsNotSet() {
        assertNull(uut.getLoops());
    }

    @Test
    void endLengthNotSet() {
        assertNull(uut.getEndLength());
    }

    @Test
    void mixingPreampLevelNotSet() {
        assertNull(uut.getMixingLevel());
    }

    @Test
    void toStringDoesNotCrash() {
        Xid6 empty = new Xid6();
        assertNotNull(empty.toString());
    }


}
