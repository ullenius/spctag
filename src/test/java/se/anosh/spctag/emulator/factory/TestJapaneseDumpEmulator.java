package se.anosh.spctag.emulator.factory;

import java.io.IOException;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import se.anosh.spctag.emulator.factory.Emulator;
import se.anosh.spctag.emulator.factory.EmulatorFactory;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;
import se.anosh.spctag.emulator.factory.ModernEmulatorFactory;
import se.anosh.spctag.emulator.factory.Name;

/**
 * Testing the DumpEmulator class
 * 
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TestJapaneseDumpEmulator {
    
    private Emulator result;
    private EmulatorFactory factory;
    
    public TestJapaneseDumpEmulator() {
    }
    
    @Before
    public void setup() throws IOException {
    	
    	factory = new ModernEmulatorFactory();
        
    }
    
    /**
     * 
     * Tests that the method returns the correct 
     * enumeration based on the code provided
     */
    @Test
    public void testValidJapaneseEmulatorCodes() {
        
        result = factory.orderEmulator(0x31, Type.JAPANESE);
        assertEquals(Name.ZSNES,result.getName());
        result = factory.orderEmulator(0x1, Type.JAPANESE);
        assertEquals(Name.ZSNES,result.getName());
        
        result = factory.orderEmulator(0x32, Type.JAPANESE);
        assertEquals(Name.Snes9x,result.getName());
        result = factory.orderEmulator(0x2, Type.JAPANESE);
        assertEquals(Name.Snes9x,result.getName());
        
        result = factory.orderEmulator(0x33, Type.JAPANESE);
        assertEquals(Name.ZST2SPC,result.getName());
        result = factory.orderEmulator(0x3, Type.JAPANESE);
        assertEquals(Name.ZST2SPC,result.getName());
         
        result = factory.orderEmulator(0x35, Type.JAPANESE);
        assertEquals(Name.SNEShout,result.getName());
        result = factory.orderEmulator(0x5, Type.JAPANESE);
        assertEquals(Name.SNEShout,result.getName());
        
        result = factory.orderEmulator(0x36, Type.JAPANESE);
        assertEquals(Name.ZSNES_W,result.getName());
        result = factory.orderEmulator(0x6, Type.JAPANESE);
        assertEquals(Name.ZSNES_W,result.getName());
        
        result = factory.orderEmulator(0x37, Type.JAPANESE);
        assertEquals(Name.Snes9xpp,result.getName());
        result = factory.orderEmulator(0x7, Type.JAPANESE);
        assertEquals(Name.Snes9xpp,result.getName());
         
        result = factory.orderEmulator(0x38, Type.JAPANESE);
        assertEquals(Name.SNESGT,result.getName());
        result = factory.orderEmulator(0x8, Type.JAPANESE);
        assertEquals(Name.SNESGT,result.getName());
        
        // 'Unknown' and 'Other'
        result = factory.orderEmulator(0x34, Type.JAPANESE);
        assertEquals(Name.Other,result.getName());
        result = factory.orderEmulator(0x4, Type.JAPANESE);
        assertEquals(Name.Other,result.getName());
        
        result = factory.orderEmulator(0x0, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName());
        result = factory.orderEmulator(0x30, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName());
    }
    
    @Test
    public void testInvalidJapaneseEmulatorCodes() {
        
        result = factory.orderEmulator(0x32, Type.JAPANESE);
        assertNotEquals(Name.ZSNES,result.getName());
        
        result = factory.orderEmulator(1337, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
        result = factory.orderEmulator(-15, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
    }
    
    
}
