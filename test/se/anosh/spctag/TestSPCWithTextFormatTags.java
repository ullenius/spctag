package se.anosh.spctag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TestSPCWithTextFormatTags {
    
    public TestSPCWithTextFormatTags() {
    }
    
    SpcFile spcFile;
    
    @Before
    public void setup() throws IOException {
        
        spcFile = new SpcFile("../text.spc");
    }
    
    @Test
    public void testFileWithValidHeader() {
        
        // first 27 of string should equal "SNES-SPC700 Sound File Data"
        String headerWithoutVersionNumber = spcFile.getHeader().substring(0,27);
        assertEquals("SNES-SPC700 Sound File Data",headerWithoutVersionNumber); // case sensitive
    }
    
    @Test(expected=IOException.class)
    public void testFileWithInvalidHeader() throws IOException {
        // tests a file that is not SPC
        spcFile = new SpcFile("../randomBytes.spc"); // will throw exception
        
    }
    
    @Test
    /**
     * SPC-files have a byte set to 26 or 27 if tags are present or not
     */
    public void testIfHeaderContainsTags() throws IOException {
        assertTrue(spcFile.isId666TagsPresent());
    }
    
    @Test
    public void testIfHeaderDoesNotContainsTags() throws IOException {
        
        spcFile = new SpcFile("../containsNoTagSetToTrue.spc");
        assertFalse(spcFile.isId666TagsPresent());
    }
    
    @Test
    public void testValidTextTagFormattedTags() throws IOException {
        System.out.println("NU KOLLAR I VALID TEXT TAG FORMAT");
        assertTrue(spcFile.isTextTagFormat());
    }
    
    @Test
    public void testIfTextTagsAreDetectedAsBinary() throws IOException {
        assertFalse(spcFile.isBinaryTagFormat());
        
    }
    
    @Test
    public void testIdenticalHashCodes() throws IOException {
        SpcFile clone = new SpcFile("../text.spc");
        assertNotSame(clone,spcFile); // don't cheat
        assertEquals(spcFile.hashCode(),clone.hashCode());
    }
    
    @Test
    public void testNotIdenticalHashCodes() throws IOException {
        
        SpcFile different = new SpcFile("../binary.spc");
        assertNotSame(different,spcFile); // object references
        assertNotEquals(different,spcFile); // while we're at it
        assertNotEquals(different.hashCode(),spcFile.hashCode());
    }
    
    @Test
    public void testEqualObjects() throws IOException {
        
        SpcFile clone = new SpcFile("../text.spc");
        assertNotSame(clone,spcFile); // no cheating
        assertEquals(clone.hashCode(),spcFile.hashCode()); // equal objects *MUST* have equals hashcodes
        assertEquals(clone,spcFile);
    }
    
    @Test
    public void testNonEqualObjects() throws IOException {
        
         SpcFile clone = new SpcFile("../binary.spc");
         assertNotEquals(clone,spcFile);
    }
    
    @Test
    public void testComparableSorting() throws IOException {
        
        SpcFile other = new SpcFile("../binary.spc");
        
        List<SpcFile> myList = new ArrayList<>();
        myList.add(other);
        myList.add(spcFile);
        myList.add(other);
        myList.add(spcFile);
        
        myList.sort(null);
        myList.forEach(System.out::println);
        assertEquals(spcFile,myList.get(0));
        assertEquals(spcFile,myList.get(1));
        assertEquals(other,myList.get(2));
        assertEquals(other,myList.get(3));
        
    }
    
    @Test
    public void testComparableWithNullValues() throws IOException {
        
        SpcFile other = new SpcFile("../binary.spc");
        
        other.setGameTitle(null);
        other.setSongTitle(null);
        other.setArtist(null);
        
        spcFile.setGameTitle(null);
        
        List<SpcFile> myList = new ArrayList<>();
        myList.add(spcFile);
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
