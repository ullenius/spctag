package se.anosh.spctag;

import static org.junit.Assert.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.util.*;

import org.junit.*;

import org.tinylog.Logger;
import se.anosh.spctag.dao.*;
import se.anosh.spctag.domain.Id666;

public class TestModelWithData {

    private static final String ROOT_PATH = "src/test/resources/spc/";

    static final String BINARY_SPC = ROOT_PATH + "binary.spc";

    private static final String BINARY_SPC_WITHOUT_DUMPED_DATE = ROOT_PATH + "binary-nodate.spc";

    static final String TEXT_SPC = ROOT_PATH + "text.spc";

    private static final String SPC_WITH_NO_ID666_TAGS = ROOT_PATH + "containsNoTagSetToTrue.spc";

    private SpcFile spcFile;
    private Id666 id666;

    @Before
    public void setup() throws IOException {
        spcFile = new SpcFile(TEXT_SPC);
        id666 = spcFile.read();
    }

    @Test
    public void headerContainsTags() {
        assertTrue(id666.hasId666Tags());
    }

    @Test
    public void headerDoesNotContainsTags() throws IOException {
        spcFile = new SpcFile(SPC_WITH_NO_ID666_TAGS);
        id666 = spcFile.read();
        assertFalse(id666.hasId666Tags());
    }

    @Test
    public void hasTextTags() {
        assertTrue(id666.isTextTagFormat());
    }

    @Test
    public void tagsAreDetectedAsBinary() {
        assertFalse(id666.isBinaryTagFormat());
    }

    @Test
    public void dumpedDateBinaryParsing() throws IOException {
        final Id666 id666 = new SpcFile(BINARY_SPC).read();
        assertEquals(Boolean.TRUE, id666.isBinaryTagFormat()); // sanity check

        final String EXPECTED = LocalDate.of(1999, Month.DECEMBER, 31)
                .toString()
                .replaceAll("\\D", "");
        assertEquals(EXPECTED, id666.getDateDumpWasCreated());
    }

    @Test
    public void binarySpcMissingDumpDate() throws IOException {
        final Id666 id666 = new SpcFile(BINARY_SPC_WITHOUT_DUMPED_DATE).read();
        assertEquals(Boolean.TRUE, id666.isBinaryTagFormat());
        assertEquals("", id666.getDateDumpWasCreated());
    }

    @Test
    public void identicalHashcodes() throws IOException {
        SpcFile cloneFile = new SpcFile(TEXT_SPC);
        Id666 clone = cloneFile.read();

        assertNotSame(clone, id666); // don't cheat
        assertEquals(id666.hashCode(), clone.hashCode());
    }

    @Test
    public void differentHashcodes() throws IOException {
        SpcFile different = new SpcFile(BINARY_SPC);
        Id666 differentId666 = different.read();

        assertNotSame(differentId666, id666);
        assertNotEquals(differentId666, id666);
        assertNotEquals(differentId666.hashCode(), id666.hashCode());
    }

    @Test
    public void equalObjecs() throws IOException {
        SpcFile cloneFile = new SpcFile(TEXT_SPC);
        Id666 clone = cloneFile.read();

        assertNotSame(clone, id666);
        assertEquals(clone.hashCode(), id666.hashCode());
        assertEquals(clone, id666);
    }

    @Test
    public void nonEqualObjects() throws IOException {
        SpcFile cloneFile = new SpcFile(BINARY_SPC);
        Id666 clone = cloneFile.read();
        assertNotEquals(clone, spcFile.read());
    }

    @Test
    public void sortingWorks() throws IOException {
        final SpcFile otherFile = new SpcFile(BINARY_SPC);
        final Id666 other = otherFile.read();

        List<Id666> myList = new LinkedList<>();
        myList.add(other);
        myList.add(id666);
        myList.add(other);
        myList.add(id666);

        myList.sort(null);
        myList.forEach(Logger::debug);
        assertEquals(id666, myList.get(0));
        assertEquals(id666, myList.get(1));
        assertEquals(other, myList.get(2));
        assertEquals(other, myList.get(3));
    }

    @Test
    public void comparatorAllowsNullValues() throws IOException {
        SpcDao otherFile = new SpcFile(BINARY_SPC);
        Id666 other = otherFile.read();

        other.setGameTitle(null);
        other.setSongTitle(null);
        other.setArtist(null);

        id666.setGameTitle(null);

        List<Id666> myList = new LinkedList<>();
        myList.add(id666);
        myList.add(other);

        myList.sort(null);

        assertNull(myList.get(0).getSongTitle());
        assertNull(myList.get(0).getGameTitle());
        assertNull(myList.get(0).getArtist());
        assertNull(myList.get(1).getGameTitle());
        assertNotNull(myList.get(1).getArtist());
        assertNotNull(myList.get(1).getSongTitle());
    }

}
