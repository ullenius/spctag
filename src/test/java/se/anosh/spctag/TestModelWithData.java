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
	
    static final String BINARY_SPC = "src/test/resources/spc/binary.spc";
    static final String TEXT_SPC = "src/test/resources/spc/text.spc";

    private static final String SPC_WITH_NO_ID666_TAGS = "src/test/resources/spc/containsNoTagSetToTrue.spc";
    
	private SpcFile spcFile;
    private Id666 id666;
    
    @Before
    public void setup() throws IOException {
        spcFile = new SpcFile(TEXT_SPC);
        id666 = spcFile.read();
    }

    @Test
    /*
     * SPC-files have a byte set to 26 or 27 if tags are present or not
     */
    public void testIfHeaderContainsTags() {
        assertTrue(id666.hasId666Tags());
    }
    
    @Test
    public void testIfHeaderDoesNotContainsTags() throws IOException {
        spcFile = new SpcFile(SPC_WITH_NO_ID666_TAGS);
        id666 = spcFile.read();
        assertFalse(id666.hasId666Tags());
    }
    
    @Test
    public void testValidTextTagFormattedTags() {
        assertTrue(id666.isTextTagFormat());
    }
    
    @Test
    public void testIfTextTagsAreDetectedAsBinary() {
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
    public void testIdenticalHashCodes() throws IOException {
        SpcFile cloneFile = new SpcFile(TEXT_SPC);
        Id666 clone = cloneFile.read();
        
        assertNotSame(clone,id666); // don't cheat
        assertEquals(id666.hashCode(),clone.hashCode());
    }
    
    @Test
    public void testNotIdenticalHashCodes() throws IOException {
        SpcFile different = new SpcFile(BINARY_SPC);
        Id666 differentId666 = different.read();
        
        assertNotSame(differentId666,id666); // object references
        assertNotEquals(differentId666,id666); // while we're at it
        assertNotEquals(differentId666.hashCode(),id666.hashCode());
    }
    
    @Test
    public void testEqualObjects() throws IOException {
        SpcFile cloneFile = new SpcFile(TEXT_SPC);
        Id666 clone = cloneFile.read();
        
        assertNotSame(clone,id666); // no cheating
        assertEquals(clone.hashCode(), id666.hashCode()); // equal objects *MUST* have equals hashcodes
        assertEquals(clone, id666);
    }
    
    @Test
    public void testNonEqualObjects() throws IOException {
         SpcFile cloneFile = new SpcFile(BINARY_SPC);
         Id666 clone = cloneFile.read();
         
         assertNotEquals(clone, spcFile.read());
    }
    
    @Test
    public void testComparableSorting() throws IOException {
        final SpcFile otherFile = new SpcFile(BINARY_SPC);
        final Id666 other = otherFile.read();
        
        List<Id666> myList = new LinkedList<>();
        myList.add(other);
        myList.add(id666);
        myList.add(other);
        myList.add(id666);
        
        myList.sort(null);
        myList.forEach(Logger::debug);
        assertEquals(id666,myList.get(0));
        assertEquals(id666,myList.get(1));
        assertEquals(other,myList.get(2));
        assertEquals(other,myList.get(3));
    }
    
    @Test
    public void testComparableWithNullValues() throws IOException {
        SpcDao otherFile = new SpcFile(BINARY_SPC); //accessing using the interface this time
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
