package se.anosh.spctag;

import org.junit.Before;
import org.junit.Test;
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

import static org.junit.Assert.*;

public class Xid6Test {

    private Path spc;
    private SpcDao dao;
    private Xid6 uut;

    @Before
    public void setup() throws IOException {
        spc = Paths.get("spc/xid6.spc");
        dao = new SpcFile(spc.toString());
        uut = dao.readXid6();
    }

    @Test
    public void dateDumpedNotSet() {
        assertNull(uut.getDate());
    }

    @Test
    public void emulatorUsedNotSet() {
        assertEquals(Name.Snes9x, uut.getEmulator().getName());
    }

    @Test
    public void gameName() {
        assertEquals("Kyouraku Sanyou Toyomaru Okumura Daiichi Maruhon Parlor Parlor! 4 CR", uut.getGame());
    }
    @Test
    public void commentsWorks() {
        assertEquals("\"Auld Lang Syne\", scottish folk song. Taken down in musical notation by Robert Burns.",
                uut.getComments());
    }

    @Test
    public void publishersNameWorks() {
        assertEquals("Nihon Telenet", uut.getPublisher());
    }

    @Test
    public void copyrightYearWorks() {
        assertEquals(Year.of(1995), uut.getYear());
    }

    @Test
    public void introLength() {
        assertEquals(Double.valueOf(12.6), uut.getIntrolength());
    }

    @Test
    public void fadeLength() {
        assertEquals(Integer.valueOf(448000), uut.getFadeLength());
    }

    @Test
    public void dateConversionWorks() {
        final int dumpedDate = 20201224;
        final LocalDate expected = LocalDate.of(2020, Month.DECEMBER, 24);
        final Xid6 uut2 = new Xid6();
        uut2.setDate(dumpedDate);
        assertEquals(expected, uut2.getDate());
    }

    @Test
    public void mutedChannelHasEightBits() {
        short mutedChannels = 0xFF;
        final Xid6 uut2 = new Xid6();
        uut2.setMutedChannels(mutedChannels);
        final String expected = "11111111";
        assertEquals(expected, uut2.getMutedChannels());
    }

    @Test
    public void parseMutedChannelsFromFile() throws IOException {
        Path spc2 = Paths.get("spc/bar.spc");
        SpcDao dao2 = new SpcFile(spc2.toString());
        Xid6 uut2 = dao2.readXid6();
        final String bitpattern = "01000011";
        assertEquals(bitpattern, uut2.getMutedChannels());
    }

    // ------------ NULL CHECKS -------------------------------------
    @Test
    public void mutedVoices() {
        assertFalse(uut.hasMutedChannels());
    }

    @Test
    public void songNameNotSet() {
        assertNull(uut.getSong());
    }

    @Test
    public void dumpersNameNotSet() {
        assertNull(uut.getDumper());
    }

    @Test
    public void artistNotSet() {
        assertNull(uut.getArtist());
    }


    @Test
    public void ostTitleNotSet() {
        assertNull(uut.getOstTitle());
    }

    @Test
    public void ostDiscNotSet() {
        assertNull(uut.getOstDisc());
    }

    @Test
    public void ostTrackNotSet() {
        assertNull(uut.getOstTrack());
    }

    @Test
    public void numberOfLoopsNotSet() {
        assertNull(uut.getLoops());
    }

    @Test
    public void endLengthNotSet() {
        assertNull(uut.getEndLength());
    }

    @Test
    public void mixingPreampLevelNotSet() {
        assertNull(uut.getMixingLevel());
    }

    @Test
    public void toStringDoesNotCrash() {
        Xid6 empty = new Xid6();
        System.out.println(empty);
    }



}
