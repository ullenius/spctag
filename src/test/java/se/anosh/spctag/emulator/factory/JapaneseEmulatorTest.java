package se.anosh.spctag.emulator.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

public class JapaneseEmulatorTest {
    
    private EmulatorFactory factory;
    
    // Magic number constants. Used by test-classes in this package
    // They don't match any emulator code. Used for testing
    // sad path
    static final int INVALID_POSITIVE_NUMBER = 1337;
    static final int INVALID_NEGATIVE_NUMBER = -15;
    
    @BeforeEach
    public void setup() {
    	factory = new ModernEmulatorFactory();
    }
    
    /**
     * 
     * Tests that the method returns the correct 
     * enumeration based on the code provided
     */
    @ParameterizedTest
    @MethodSource("provideStringsForIsBlank")
    public void testValidJapaneseEmulatorCodes(final int code, final Name expected) {
        final Emulator emulator = factory.orderEmulator(code, Type.JAPANESE);
        final Name actual = emulator.getName();
        assertEquals(expected, actual);
    }

    private static Object[][] validCodes() {
        return new Object[][]{
                // input code, expected
                {ZSNES_TEXT, Name.ZSNES},
                {ZSNES_BINARY, Name.ZSNES},

                {SNES9X_TEXT, Name.Snes9x},
                {SNES9X_BINARY, Name.Snes9x},

                {ZST2SPC_TEXT, Name.ZST2SPC},
                {ZST2SPC_BINARY, Name.ZST2SPC},

                {SNESHOUT_TEXT, Name.SNEShout},
                {SNESHOUT_BINARY, Name.SNEShout},

                {ZSNES_W_TEXT, Name.ZSNES_W},
                {ZSNES_W_BINARY, Name.ZSNES_W},

                { SNES9X_TEXT, Name.Snes9x},
                { SNES9X_BINARY, Name.Snes9x},

                { SNES9XPP_TEXT, Name.Snes9xpp},
                { SNES9XPP_BINARY, Name.Snes9xpp},

                { SNESGT_TEXT, Name.SNESGT},
                { SNESGT_BINARY, Name.SNESGT},

                { OTHER_TEXT, Name.Other},
                { OTHER_BINARY, Name.Other},

                { UNKNOWN_TEXT, Name.Unknown},
                { UNKNOWN_BINARY, Name.Unknown},
        };
    }
    
    @Test
    public void testInvalidJapaneseEmulatorCodes() {
        Emulator result;
        result = factory.orderEmulator(SNES9X_TEXT, Type.JAPANESE);
        assertNotEquals(Name.ZSNES,result.getName());
        
        // sad path testing
        result = factory.orderEmulator(INVALID_POSITIVE_NUMBER, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName()); // unknown is default
        
        result = factory.orderEmulator(INVALID_NEGATIVE_NUMBER, Type.JAPANESE);
        assertEquals(Name.Unknown,result.getName());
        
    }
    
    
}
