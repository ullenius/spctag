package se.anosh.spctag.emulator.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static se.anosh.spctag.emulator.factory.LegacyEmulator.*;
import static se.anosh.spctag.emulator.factory.JapaneseEmulatorTest.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

public class LegacyEmulatorTest {

    @ParameterizedTest
    @MethodSource("validCodes")
    void testValidLegacyEmulatorCodes(final int code, final Name expected) {
        final Emulator emulator = EmulatorFactory.createEmulator(code, Type.LEGACY);
        final Name actual = emulator.getName();
        assertEquals(expected, actual);
    }

    private static Object[][] validCodes() {
        return new Object[][]{
                // input code, expected
                {UNKNOWN, Name.Unknown},
                {ZSNES, Name.ZSNES},
                {SNES9x, Name.Snes9x}
        };
    }

    @ParameterizedTest
    @MethodSource("invalidCodes")
    void testInvalidLegacyEmulatorCodes(final int code) {
        final Emulator emulator = EmulatorFactory.createEmulator(code, Type.LEGACY);
        final Name actual = emulator.getName();
        assertEquals(Name.Unknown, actual);
    }

    private static Object[][] invalidCodes() {
        return new Object[][]{
                {INVALID_POSITIVE_NUMBER},
                {INVALID_NEGATIVE_NUMBER},
                {JapaneseEmulator.SNES9X_TEXT} // valid value from wrong spec
        };
    }

}