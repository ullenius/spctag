package se.anosh.spctag;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import se.anosh.spctag.emulator.DumpEmulator;
import se.anosh.spctag.emulator.Emulator;

/**
 * Testing the DumpEmulator class
 * 
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TestDumpEmulator {
    
    private Emulator result;
    
    public TestDumpEmulator() {
    }
    
    @Before
    public void setup() throws IOException {
        
    }
    
    /**
     * 
     * Tests that the method returns the correct 
     * enumeration based on the code provided
     */
    @Test
    public void testValidEmulatorCodes() {
        
        result = DumpEmulator.getName(0x31);
        assertEquals(Emulator.ZSNES,result);
        result = DumpEmulator.getName(0x1);
        assertEquals(Emulator.ZSNES,result);
        
        result = DumpEmulator.getName(0x32);
        assertEquals(Emulator.Snes9x,result);
        result = DumpEmulator.getName(0x2);
        assertEquals(Emulator.Snes9x,result);
        
        result = DumpEmulator.getName(0x33);
        assertEquals(Emulator.ZST2SPC,result);
        result = DumpEmulator.getName(0x3);
        assertEquals(Emulator.ZST2SPC,result);
         
        result = DumpEmulator.getName(0x35);
        assertEquals(Emulator.SNEShout,result);
        result = DumpEmulator.getName(0x5);
        assertEquals(Emulator.SNEShout,result);
        
        result = DumpEmulator.getName(0x36);
        assertEquals(Emulator.ZSNES_W,result);
        result = DumpEmulator.getName(0x6);
        assertEquals(Emulator.ZSNES_W,result);
        
        result = DumpEmulator.getName(0x37);
        assertEquals(Emulator.Snes9xpp,result);
        result = DumpEmulator.getName(0x7);
        assertEquals(Emulator.Snes9xpp,result);
         
        result = DumpEmulator.getName(0x38);
        assertEquals(Emulator.SNESGT,result);
        result = DumpEmulator.getName(0x8);
        assertEquals(Emulator.SNESGT,result);
        
        // 'Unknown' and 'Other'
        result = DumpEmulator.getName(0x34);
        assertEquals(Emulator.Other,result);
        result = DumpEmulator.getName(0x4);
        assertEquals(Emulator.Other,result);
        
        // Unknown is only defined once by japanese spec
        result = DumpEmulator.getName(0x0);
        assertEquals(Emulator.Unknown,result);
    }
    
    @Test
    public void testInvalidEmulatorCodes() {
        
        result = DumpEmulator.getName(0x32);
        assertNotEquals(Emulator.ZSNES,result);
        
        result = DumpEmulator.getName(1337);
        assertEquals(Emulator.Unknown,result); // unknown is default
        
        result = DumpEmulator.getName(-15); // possible garbage input
        assertEquals(Emulator.Unknown,result); // unknown is default
        
    }
    
    
}
