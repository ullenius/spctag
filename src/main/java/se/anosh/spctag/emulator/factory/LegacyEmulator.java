package se.anosh.spctag.emulator.factory;

import java.util.Objects;

/**
 *
 * This class matches the code provided according to the legacy spec written by
 * creaothceann on 2006-02-27 21:48
 * ---
 * Some values don't overlap in the Japanese spec. That's why I created two
 * different implementations.
 * 
 */
final class LegacyEmulator implements Emulator {
    
    // binary and text tags share the same values in this spec
    static final int UNKNOWN = 0;
    static final int ZSNES = 1;
    static final int SNES9x = 2;
    
    private final int code;
    private final Name name;

    LegacyEmulator(int code) {
        this.code = code;
        name = switch (code) {
            case ZSNES -> Name.ZSNES;
            case SNES9x -> Name.Snes9x;
            default -> Name.Unknown;
        };
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
