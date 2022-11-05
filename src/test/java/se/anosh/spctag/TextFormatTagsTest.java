package se.anosh.spctag;

import static org.junit.Assert.assertEquals;
import static se.anosh.spctag.TestModelWithData.TEXT_SPC;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Id666;

public class TextFormatTagsTest {
    
    private SpcFile spcFile;
    private Id666 id666;

    private static final String SPC_RANDOM_BYTES = "src/test/resources/spc/randomBytes.spc";


    @Before
    public void setup() throws IOException {
        spcFile = new SpcFile(TEXT_SPC);
        id666 = spcFile.read();
    }
    
    @Test
    public void testFileWithValidHeader() {
        // first 27 of string should equal "SNES-SPC700 Sound File Data"
        String headerWithoutVersionNumber = id666.getHeader().substring(0, 27);
        assertEquals("SNES-SPC700 Sound File Data", headerWithoutVersionNumber); // case sensitive
    }
    
    @Test(expected=IOException.class)
    public void testFileWithInvalidHeader() throws IOException {
        // tests a file that is not SPC
        spcFile = new SpcFile(SPC_RANDOM_BYTES);
    }
    
    
}
