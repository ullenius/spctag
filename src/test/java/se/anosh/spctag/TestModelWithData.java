package se.anosh.spctag;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import se.anosh.spctag.dao.Id666;
import se.anosh.spctag.dao.SpcDao;
import se.anosh.spctag.dao.SpcFileImplementation;

public class TestModelWithData {
	
	
    public TestModelWithData() {
		super();
	}

	private SpcFileImplementation spcFile;
    private Id666 id666;
    
    
    @Before
    public void setup() throws IOException {
        
        spcFile = new SpcFileImplementation("spc/text.spc");
        id666 = spcFile.read();
    }
	

    @Test
    /**
     * SPC-files have a byte set to 26 or 27 if tags are present or not
     */
    public void testIfHeaderContainsTags() throws IOException {
        assertTrue(id666.hasId666Tags());
    }
    
    @Test
    public void testIfHeaderDoesNotContainsTags() throws IOException {
        
        spcFile = new SpcFileImplementation("spc/containsNoTagSetToTrue.spc");
        id666 = spcFile.read();
        assertFalse(id666.hasId666Tags());
    }
    
    @Test
    public void testValidTextTagFormattedTags() throws IOException {
        assertTrue(id666.isTextTagFormat());
    }
    
    @Test
    public void testIfTextTagsAreDetectedAsBinary() throws IOException {
        assertFalse(id666.isBinaryTagFormat());
        
    }
    
    @Test
    public void testIdenticalHashCodes() throws IOException {
        SpcFileImplementation cloneFile = new SpcFileImplementation("spc/text.spc");
        Id666 clone = cloneFile.read();
        
        assertNotSame(clone,id666); // don't cheat
        assertEquals(id666.hashCode(),clone.hashCode());
    }
    
    @Test
    public void testNotIdenticalHashCodes() throws IOException {
        
        SpcFileImplementation different = new SpcFileImplementation("spc/binary.spc");
        Id666 differentId666 = different.read();
        
        assertNotSame(differentId666,id666); // object references
        assertNotEquals(differentId666,id666); // while we're at it
        assertNotEquals(differentId666.hashCode(),id666.hashCode());
    }
    
    @Test
    public void testEqualObjects() throws IOException {
        
        SpcFileImplementation cloneFile = new SpcFileImplementation("spc/text.spc");
        Id666 clone = cloneFile.read();
        
        assertNotSame(clone,id666); // no cheating
        assertEquals(clone.hashCode(),id666.hashCode()); // equal objects *MUST* have equals hashcodes
        assertEquals(clone,id666);
    }
    
    @Test
    public void testNonEqualObjects() throws IOException {
        
         SpcFileImplementation cloneFile = new SpcFileImplementation("spc/binary.spc");
         Id666 clone = cloneFile.read();
         
         assertNotEquals(clone,spcFile);
    }
    
    @Test
    public void testComparableSorting() throws IOException {
        
        SpcFileImplementation otherFile = new SpcFileImplementation("spc/binary.spc");
        Id666 other = otherFile.read();
        
        List<Id666> myList = new ArrayList<>();
        myList.add(other);
        myList.add(id666);
        myList.add(other);
        myList.add(id666);
        
        myList.sort(null);
        myList.forEach(System.out::println);
        assertEquals(id666,myList.get(0));
        assertEquals(id666,myList.get(1));
        assertEquals(other,myList.get(2));
        assertEquals(other,myList.get(3));
        
    }
    
    @Test
    public void testComparableWithNullValues() throws IOException {
        
        SpcDao otherFile = new SpcFileImplementation("spc/binary.spc"); //accessing using the interface this time
        Id666 other = otherFile.read();
        
        other.setGameTitle(null);
        other.setSongTitle(null);
        other.setArtist(null);
        
        id666.setGameTitle(null);
        
        List<Id666> myList = new ArrayList<>();
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
