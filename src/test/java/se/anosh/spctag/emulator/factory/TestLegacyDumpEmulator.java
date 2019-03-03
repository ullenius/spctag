package se.anosh.spctag.emulator.factory;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import se.anosh.spctag.emulator.factory.Emulator;
import se.anosh.spctag.emulator.factory.EmulatorFactory;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;
import se.anosh.spctag.emulator.factory.ModernEmulatorFactory;
import se.anosh.spctag.emulator.factory.Name;
import static se.anosh.spctag.emulator.factory.LegacyEmulator.*;
import se.anosh.spctag.emulator.factory.JapaneseEmulator;
import static se.anosh.spctag.emulator.factory.TestJapaneseDumpEmulator.INVALID_NEGATIVE_NUMBER;
import static se.anosh.spctag.emulator.factory.TestJapaneseDumpEmulator.INVALID_POSITIVE_NUMBER;

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