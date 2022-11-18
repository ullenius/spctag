package se.anosh.spctag.emulator.factory;

import java.util.Map;
import java.util.Objects;

/**
 *
 * This class matches the code provided according to the legacy spec
 * made by creaothceann on 2006-02-27 21:48
 * ---
 * Some values don't overlap in the Japanese spec. That's why
 * I created two different implementations.
 * 
 */
final class LegacyEmulator implements Emulator {
    
    // binary and text tags share the same values in this spec
    static final int UNKNOWN = 0;
    static final int ZSNES = 1;
    static final int SNES9x = 2;
    
    private static final Map<Integer, Name> emulatorMap = Map.of(
        UNKNOWN, Name.Unknown,
        ZSNES, Name.ZSNES,
        SNES9x,Name.Snes9x
    );

    private final int code;
    private final Name name;

    LegacyEmulator(int code) {
        this.code = code;
        name = Objects.requireNonNullElse(emulatorMap.get(code), Name.Unknown);
    }


    @Override
    public Name getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Emulator{" + "name=" + name + ", offset= 0x" + Integer.toHexString(code) + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LegacyEmulator that = (LegacyEmulator) o;
        return code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
