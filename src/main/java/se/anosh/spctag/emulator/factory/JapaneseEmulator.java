package se.anosh.spctag.emulator.factory;

import java.util.Map;
import java.util.Objects;


final class JapaneseEmulator implements Emulator {
    
    // emulator values for the TEXT tag offset
    static final int ZSNES_TEXT = 0x31;
    static final int SNES9X_TEXT = 0x32;
    static final int ZST2SPC_TEXT = 0x33;
    
    static final int OTHER_TEXT = 0x34;
    static final int SNESHOUT_TEXT = 0x35;
    static final int ZSNES_W_TEXT = 0x36;
    
    static final int SNES9XPP_TEXT = 0x37;
    static final int SNESGT_TEXT = 0x38;
    
    // emulator values for the BINARY tag offset
    static final int UNKNOWN_BINARY = 0x00;
    static final int UNKNOWN_TEXT = 0x30;
    
    static final int ZSNES_BINARY = 0x01;
    static final int SNES9X_BINARY = 0x02;
    
    static final int ZST2SPC_BINARY = 0x03;
    static final int OTHER_BINARY = 0x04;
    static final int SNESHOUT_BINARY = 0x05;
    
    static final int ZSNES_W_BINARY = 0x06;
    static final int SNES9XPP_BINARY = 0x07;
    static final int SNESGT_BINARY= 0x08;
    
    static final Map<Integer,Name> emulatorMap;
    
    static {
        emulatorMap = Map.ofEntries(
    	Map.entry(ZSNES_TEXT, Name.ZSNES),
    	Map.entry(ZSNES_BINARY, Name.ZSNES),
    	
    	Map.entry(SNES9X_TEXT, Name.Snes9x),
    	Map.entry(SNES9X_BINARY, Name.Snes9x),
    	
    	Map.entry(ZST2SPC_TEXT, Name.ZST2SPC),
    	Map.entry(ZST2SPC_BINARY, Name.ZST2SPC),
    	
    	Map.entry(OTHER_TEXT, Name.Other),
    	Map.entry(OTHER_BINARY, Name.Other),
    	
    	Map.entry(UNKNOWN_BINARY, Name.Unknown),
        Map.entry(UNKNOWN_TEXT, Name.Unknown),
    	
    	Map.entry(SNESHOUT_TEXT, Name.SNEShout),
    	Map.entry(SNESHOUT_BINARY, Name.SNEShout),
    	
    	Map.entry(ZSNES_W_TEXT, Name.ZSNES_W),
    	Map.entry(ZSNES_W_BINARY, Name.ZSNES_W),
    	
    	Map.entry(SNES9XPP_TEXT, Name.Snes9xpp),
    	Map.entry(SNES9XPP_BINARY, Name.Snes9xpp),
    	
    	Map.entry(SNESGT_TEXT, Name.SNESGT),
    	Map.entry(SNESGT_BINARY, Name.SNESGT));
    }

    private final int code;
    private final Name name;
    
    JapaneseEmulator(int code) {
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
        JapaneseEmulator that = (JapaneseEmulator) o;
        return code == that.code;
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }
}
