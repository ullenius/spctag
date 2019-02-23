package se.anosh.spctag.emulator;

/**
 * 
 * This class only contains static methods
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public final class DumpEmulator {
    
     private DumpEmulator() {
        throw new AssertionError(); // exists to prevent instantiation
    }
    
    // emulator values for the TEXT tag offset
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
    private static final int ZSNES_BINARY = 0x1;
    private static final int SNES9X_BINARY = 0x2;
    
    private static final int ZST2SPC_BINARY = 0x3;
    private static final int OTHER_BINARY = 0x4;
    private static final int SNESHOUT_BINARY = 0x5;
    
    private static final int ZSNES_W_BINARY = 0x6;
    private static final int SNES9XPP_BINARY = 0x7;
    private static final int SNESGT_BINARY= 0x8;
    
    /**
     * 
     * Accepts a code present at the offset defined in
     * the specification that contains an (unsigned?) byte
     * that determines the emulator used to create the dump.
     * 
     * Matches the code provided (according to the japanese spec)
     * and returns a matching enumeration.
     * 
     * @param code
     * @return 
     */
    public static Emulator getName(final int code) {
        
        Emulator result;
        
        switch (code) {
            case ZSNES_TEXT:
            case ZSNES_BINARY:
                result = Emulator.ZSNES;
                break;
            case SNES9X_BINARY:
            case SNES9X_TEXT:
                result = Emulator.Snes9x;
                break;
            case ZST2SPC_BINARY:
            case ZST2SPC_TEXT:
                result = Emulator.ZST2SPC;
                break;
            case OTHER_TEXT:
            case OTHER_BINARY:
                result = Emulator.Other;
                break;
            case SNESHOUT_TEXT:
            case SNESHOUT_BINARY:
                result = Emulator.SNEShout;
                break;
            case UNKNOWN_BINARY:
                result = Emulator.Unknown;
                break;
            case ZSNES_W_TEXT:
            case ZSNES_W_BINARY:
                result = Emulator.ZSNES_W;
                break;
            case SNES9XPP_TEXT:
            case SNES9XPP_BINARY:
                result = Emulator.Snes9xpp;
                break;
            case SNESGT_BINARY:
            case SNESGT_TEXT:
                result = Emulator.SNESGT;
                break;
            default:
                result = Emulator.Unknown; 
        }
        return result;
    }
    
}
