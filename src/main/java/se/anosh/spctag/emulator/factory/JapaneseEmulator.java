package se.anosh.spctag.emulator.factory;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class JapaneseEmulator extends Emulator {
    
    private static final int ZSNES_TEXT = 0x31;
    private static final int SNES9X_TEXT = 0x32;
    private static final int ZST2SPC_TEXT = 0x33;
    
    private static final int OTHER_TEXT = 0x34;
    private static final int SNESHOUT_TEXT = 0x35;
    private static final int ZSNES_W_TEXT = 0x36;
    
    private static final int SNES9XPP_TEXT = 0x37;
    private static final int SNESGT_TEXT = 0x38;
    
    // emulator values for the BINARY tag offset
    private static final int UNKNOWN_BINARY = 0x0;
    private static final int UNKNOWN_TEXT = 0x30;
    
    private static final int ZSNES_BINARY = 0x1;
    private static final int SNES9X_BINARY = 0x2;
    
    private static final int ZST2SPC_BINARY = 0x3;
    private static final int OTHER_BINARY = 0x4;
    private static final int SNESHOUT_BINARY = 0x5;
    
    private static final int ZSNES_W_BINARY = 0x6;
    private static final int SNES9XPP_BINARY = 0x7;
    private static final int SNESGT_BINARY= 0x8;
    
    private static final Map<Integer,Name> emulatorMap = new HashMap<>();
    
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
