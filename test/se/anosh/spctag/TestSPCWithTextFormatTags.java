package se.anosh.spctag;

import java.io.IOException;
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
        assertTrue(headerWithoutVersionNumber.equalsIgnoreCase("SNES-SPC700 Sound File Data"));
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
        assertTrue(spcFile.containsID666Tags());
    }
    
    @Test
    public void testIfHeaderDoesNotContainsTags() throws IOException {
        
        spcFile = new SpcFile("../containsNoTagSetToTrue.spc");
        assertFalse(spcFile.containsID666Tags());
    }
    
    @Test
    public void testValidTextTagFormattedTags() throws IOException {
        System.out.println("NU KOLLAR I VALID TEXT TAG FORMAT");
        assertTrue(spcFile.hasTextTagFormat());
    }
    
    @Test
    public void testIfTextTagsAreDetectedAsBinary() throws IOException {
        assertFalse(spcFile.hasBinaryTagFormat());
        
    }
    
    
    
}
