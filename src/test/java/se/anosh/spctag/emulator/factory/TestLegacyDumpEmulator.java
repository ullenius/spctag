package se.anosh.spctag.emulator.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static se.anosh.spctag.emulator.factory.LegacyEmulator.SNES9x;
import static se.anosh.spctag.emulator.factory.LegacyEmulator.UNKNOWN;
import static se.anosh.spctag.emulator.factory.LegacyEmulator.ZSNES;
import static se.anosh.spctag.emulator.factory.TestJapaneseDumpEmulator.INVALID_NEGATIVE_NUMBER;
import static se.anosh.spctag.emulator.factory.TestJapaneseDumpEmulator.INVALID_POSITIVE_NUMBER;

import org.junit.Before;
import org.junit.Test;

import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

/**
 * Testing the LegacyEmulator class
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
    	
    	 result = factory.orderEmulator(UNKNOWN, Type.LEGACY);
         assertEquals(Name.Unknown,result.getName());
         
         result = factory.orderEmulator(ZSNES, Type.LEGACY);
         assertEquals(Name.ZSNES,result.getName());
         
         result = factory.orderEmulator(SNES9x, Type.LEGACY);
         assertEquals(Name.Snes9x,result.getName());
    }
    
    @Test
    public void testInvalidLegacyEmulatorCodes() {
        
        result = factory.orderEmulator(JapaneseEmulator.SNES9X_TEXT, Type.LEGACY); // testing a valid value from the jp-spec
        assertNotEquals(Name.ZSNES,result.getName());
        
        result = factory.orderEmulator(INVALID_POSITIVE_NUMBER, Type.LEGACY);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
        result = factory.orderEmulator(INVALID_NEGATIVE_NUMBER, Type.LEGACY);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
    }
    
    
    
}