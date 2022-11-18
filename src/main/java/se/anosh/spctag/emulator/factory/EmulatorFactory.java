package se.anosh.spctag.emulator.factory;


import java.util.Objects;

public final class EmulatorFactory {
    
    public enum Type {
        JAPANESE,
        LEGACY
    }

    private EmulatorFactory() {
        throw new AssertionError("Cannot be instantiated");
    }
    // this method act as a factory
    public static Emulator createEmulator(final int magicNumber, Type style) {
        Objects.requireNonNull(style, "Style cannot be null");
        return style == Type.JAPANESE
                ? new JapaneseEmulator(magicNumber)
                : new LegacyEmulator(magicNumber);
    }

}
