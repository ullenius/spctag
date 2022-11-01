package se.anosh.spctag.emulator.factory;

import static org.junit.Assert.*;
import static se.anosh.spctag.emulator.factory.LegacyEmulator.*;
import static se.anosh.spctag.emulator.factory.TestJapaneseDumpEmulator.*;

import org.junit.*;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

public class TestLegacyDumpEmulator {
    
    private Emulator result;
    private EmulatorFactory factory;
    
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