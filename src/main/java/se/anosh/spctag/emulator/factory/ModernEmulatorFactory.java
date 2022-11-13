package se.anosh.spctag.emulator.factory;

import java.util.Objects;

public final class ModernEmulatorFactory extends EmulatorFactory {
    
    /**
     * 
     * TODO: Add String parameter (or enum?) to decide which
     * type to create. At the moment there is only one type of
     * class that the Japanese factory creates :)
     * 
     */
    @Override
    protected Emulator createEmulator(final int magicNumber, Type style) {
        Objects.requireNonNull(style, "Style cannot be null");
        return style == Type.JAPANESE
                ? new JapaneseEmulator(magicNumber)
                : new LegacyEmulator(magicNumber);
    }

    
}
