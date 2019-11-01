package se.anosh.spctag.emulator.factory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
// contains constants, package private
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.OTHER_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.OTHER_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNES9XPP_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNES9XPP_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNES9X_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNES9X_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNESGT_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNESGT_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNESHOUT_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.SNESHOUT_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.UNKNOWN_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.UNKNOWN_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.ZSNES_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.ZSNES_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.ZSNES_W_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.ZSNES_W_TEXT;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.ZST2SPC_BINARY;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.ZST2SPC_TEXT;

import org.junit.Before;
import org.junit.Test;

import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

/**
 * Testing the JapaneseEmulator class
 * 
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TestJapaneseDumpEmulator {
    
    private Emulator result;
    private EmulatorFactory factory;
    
    // Magic number constants. Used by test-classes in this package
    // They don't match any emulator code. Used for testing
    // sad path
    static int INVALID_POSITIVE_NUMBER = 1337;
    static int INVALID_NEGATIVE_NUMBER = -15;
    
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
    public void testValidJapaneseEmulatorCodes() {
        
        result = factory.orderEmulator(ZSNES_TEXT, Type.JAPANESE);
        assertEquals(Name.ZSNES,result.getName());
        result = factory.orderEmulator(ZSNES_BINARY, Type.JAPANESE);
        assertEquals(Name.ZSNES,result.getName());
        
        result = factory.orderEmulator(SNES9X_TEXT, Type.JAPANESE);
        assertEquals(Name.Snes9x,result.getName());
        result = factory.orderEmulator(SNES9X_BINARY, Type.JAPANESE);
        assertEquals(Name.Snes9x,result.getName());
        
        result = factory.orderEmulator(ZST2SPC_TEXT, Type.JAPANESE);
        assertEquals(Name.ZST2SPC,result.getName());
        result = factory.orderEmulator(ZST2SPC_BINARY, Type.JAPANESE);
        assertEquals(Name.ZST2SPC,result.getName());
         
        result = factory.orderEmulator(SNESHOUT_TEXT, Type.JAPANESE);
        assertEquals(Name.SNEShout,result.getName());
        result = factory.orderEmulator(SNESHOUT_BINARY, Type.JAPANESE);
        assertEquals(Name.SNEShout,result.getName());
        
        result = factory.orderEmulator(ZSNES_W_TEXT, Type.JAPANESE);
        assertEquals(Name.ZSNES_W,result.getName());
        result = factory.orderEmulator(ZSNES_W_BINARY, Type.JAPANESE);
        assertEquals(Name.ZSNES_W,result.getName());
        
        result = factory.orderEmulator(SNES9XPP_TEXT, Type.JAPANESE);
        assertEquals(Name.Snes9xpp,result.getName());
        result = factory.orderEmulator(SNES9XPP_BINARY, Type.JAPANESE);
        assertEquals(Name.Snes9xpp,result.getName());
         
        result = factory.orderEmulator(SNESGT_TEXT, Type.JAPANESE);
        assertEquals(Name.SNESGT,result.getName());
        result = factory.orderEmulator(SNESGT_BINARY, Type.JAPANESE);
        assertEquals(Name.SNESGT,result.getName());
        
        // 'Unknown' and 'Other'
        result = factory.orderEmulator(OTHER_TEXT, Type.JAPANESE);
        assertEquals(Name.Other,result.getName());
        result = factory.orderEmulator(OTHER_BINARY, Type.JAPANESE);
        assertEquals(Name.Other,result.getName());
        
        result = factory.orderEmulator(UNKNOWN_TEXT, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName());
        result = factory.orderEmulator(UNKNOWN_BINARY, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName());
    }
    
    @Test
    public void testInvalidJapaneseEmulatorCodes() {
        
        result = factory.orderEmulator(SNES9X_TEXT, Type.JAPANESE);
        assertNotEquals(Name.ZSNES,result.getName());
        
        // sad path testing
        result = factory.orderEmulator(INVALID_POSITIVE_NUMBER, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
        result = factory.orderEmulator(INVALID_NEGATIVE_NUMBER, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName());
        
    }
    
    
}
