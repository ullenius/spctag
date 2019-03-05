package se.anosh.spctag.emulator.factory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * The constants are made package private to make them
 * accessible from the JUnit-tests
 * 
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class JapaneseEmulator extends Emulator {
    
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
    static final int UNKNOWN_BINARY = 0x0;
    static final int UNKNOWN_TEXT = 0x30;
    
    static final int ZSNES_BINARY = 0x1;
    static final int SNES9X_BINARY = 0x2;
    
    static final int ZST2SPC_BINARY = 0x3;
    static final int OTHER_BINARY = 0x4;
    static final int SNESHOUT_BINARY = 0x5;
    
    static final int ZSNES_W_BINARY = 0x6;
    static final int SNES9XPP_BINARY = 0x7;
    static final int SNESGT_BINARY= 0x8;
    
    static final Map<Integer,Name> emulatorMap = new HashMap<>();
    
    static {
    	emulatorMap.put(ZSNES_TEXT, Name.ZSNES);
    	emulatorMap.put(ZSNES_BINARY, Name.ZSNES);
    	
    	emulatorMap.put(SNES9X_TEXT, Name.Snes9x);
    	emulatorMap.put(SNES9X_BINARY, Name.Snes9x);
    	
    	emulatorMap.put(ZST2SPC_TEXT, Name.ZST2SPC);
    	emulatorMap.put(ZST2SPC_BINARY, Name.ZST2SPC);
    	
    	emulatorMap.put(OTHER_TEXT, Name.Other);
    	emulatorMap.put(OTHER_BINARY, Name.Other);
    	
    	emulatorMap.put(UNKNOWN_BINARY, Name.Unknown);
        emulatorMap.put(UNKNOWN_TEXT, Name.Unknown);
    	
    	emulatorMap.put(SNESHOUT_TEXT, Name.SNEShout);
    	emulatorMap.put(SNESHOUT_BINARY, Name.SNEShout);
    	
    	emulatorMap.put(ZSNES_W_TEXT, Name.ZSNES_W);
    	emulatorMap.put(ZSNES_W_BINARY, Name.ZSNES_W);
    	
    	emulatorMap.put(SNES9XPP_TEXT, Name.Snes9xpp);
    	emulatorMap.put(SNES9XPP_BINARY, Name.Snes9xpp);
    	
    	emulatorMap.put(SNESGT_TEXT, Name.SNESGT);
    	emulatorMap.put(SNESGT_BINARY, Name.SNESGT);
    }
    
    /**
     * 
     * Matches the code provided (according to the japanese spec)
     * using an internal HashMap
     * 
     * @param code
     * @return 
     */
    JapaneseEmulator(Integer code) {
        super(emulatorMap.get(code), code);
    }
    
}
