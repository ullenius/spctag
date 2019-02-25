package se.anosh.spctag.emulator.factory;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import se.anosh.spctag.emulator.factory.Emulator;
import se.anosh.spctag.emulator.factory.EmulatorFactory;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;
import se.anosh.spctag.emulator.factory.ModernEmulatorFactory;
import se.anosh.spctag.emulator.factory.Name;

/**
 * Testing the JapaneseEmulator class
 * 
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TestLegacyDumpEmulator {
    
    private Emulator result;
    private EmulatorFactory factory;
    
    public TestLegacyDumpEmulator() {
    }
    
    @Before
    public void setup() {
    	
    	factory = new ModernEmulatorFactory();
        
    }
    
    /**
     * 
     * Tests that the method returns the correct 
     * enumeration based on the code provided
     */
    @Test
    public void testValidLegacyEmulatorCodes() {
    	
    	 result = factory.orderEmulator(0x0, Type.LEGACY);
         assertEquals(Name.Unknown,result.getName());
         
         result = factory.orderEmulator(0x1, Type.LEGACY);
         assertEquals(Name.ZSNES,result.getName());
         
         result = factory.orderEmulator(0x2, Type.LEGACY);
         assertEquals(Name.Snes9x,result.getName());
    }
    
    @Test
    public void testInvalidLegacyEmulatorCodes() {
        
        result = factory.orderEmulator(0x32, Type.LEGACY);
        assertNotEquals(Name.ZSNES,result.getName());
        
        result = factory.orderEmulator(1337, Type.LEGACY);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
        result = factory.orderEmulator(-15, Type.LEGACY);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
    }
    
    
    
}