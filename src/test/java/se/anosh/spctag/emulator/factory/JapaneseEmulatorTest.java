package se.anosh.spctag.emulator.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.anosh.spctag.emulator.factory.JapaneseEmulator.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

public class JapaneseEmulatorTest {

    static final int INVALID_POSITIVE_NUMBER = 1337;
    static final int INVALID_NEGATIVE_NUMBER = -15;

    @ParameterizedTest
    @MethodSource("validCodes")
    public void japaneseEmulatorCodeMappings(final int code, final Name expected) {
        final EmulatorI emulator = EmulatorFactory.createEmulator(code, Type.JAPANESE);
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

                {SNES9X_TEXT, Name.Snes9x},
                {SNES9X_BINARY, Name.Snes9x},

                {SNES9XPP_TEXT, Name.Snes9xpp},
                {SNES9XPP_BINARY, Name.Snes9xpp},

                {SNESGT_TEXT, Name.SNESGT},
                {SNESGT_BINARY, Name.SNESGT},

                {OTHER_TEXT, Name.Other},
                {OTHER_BINARY, Name.Other},

                {UNKNOWN_TEXT, Name.Unknown},
                {UNKNOWN_BINARY, Name.Unknown},
        };
    }

    @ParameterizedTest
    @MethodSource("invalidCodes")
    public void testInvalidJapaneseEmulatorCodes(final int code) {
        final EmulatorI emulator = EmulatorFactory.createEmulator(code, Type.JAPANESE);
        final Name actual = emulator.getName();
        assertEquals(Name.Unknown, actual);
    }

    private static Object[][] invalidCodes() {
        return new Object[][]{
                {INVALID_POSITIVE_NUMBER},
                {INVALID_NEGATIVE_NUMBER},
        };
    }

}
