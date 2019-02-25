package se.anosh.spctag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import se.anosh.spctag.dao.Id666;
import se.anosh.spctag.dao.SpcDao;
import se.anosh.spctag.dao.SpcFileImplementation;

import static org.junit.Assert.*;
import org.junit.Before;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TestSPCWithTextFormatTags {
    
    public TestSPCWithTextFormatTags() {
    }
    
    SpcFileImplementation spcFile;
    Id666 id666;
    
    
    @Before
    public void setup() throws IOException {
        
        spcFile = new SpcFileImplementation("spc/text.spc");
        id666 = spcFile.read();
        
    }
    
    @Test
    public void testFileWithValidHeader() {
        
        // first 27 of string should equal "SNES-SPC700 Sound File Data"
        String headerWithoutVersionNumber = id666.getHeader().substring(0,27);
        assertEquals("SNES-SPC700 Sound File Data",headerWithoutVersionNumber); // case sensitive
    }
    
    @Test(expected=IOException.class)
    public void testFileWithInvalidHeader() throws IOException {
        // tests a file that is not SPC
        spcFile = new SpcFileImplementation("spc/randomBytes.spc"); // will throw exception
        
    }
    
    
    
    
}
