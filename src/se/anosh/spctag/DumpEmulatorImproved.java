package se.anosh.spctag;

import static se.anosh.spctag.Emulator.Other;
import static se.anosh.spctag.Emulator.SNESGT;
import static se.anosh.spctag.Emulator.SNEShout;
import static se.anosh.spctag.Emulator.Snes9x;
import static se.anosh.spctag.Emulator.Snes9xpp;
import static se.anosh.spctag.Emulator.Unknown;
import static se.anosh.spctag.Emulator.ZSNES;
import static se.anosh.spctag.Emulator.ZSNES_W;
import static se.anosh.spctag.Emulator.ZST2SPC;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class DumpEmulatorImproved {
    
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
    
    private DumpEmulatorImproved() {
        throw new AssertionError(); // exists to prevent instantiation
    }
    
    public Emulator getEmulatorUsed(int code) {
        
        Emulator result;
        
        switch (code) {
            
            case ZSNES_TEXT:
            case ZSNES_BINARY:
                result = ZSNES;
                break;
            case SNES9X_BINARY:
            case SNES9X_TEXT:
                result = Snes9x;
                break;
            case ZST2SPC_BINARY:
            case ZST2SPC_TEXT:
                result = ZST2SPC;
                break;
            case OTHER_TEXT:
            case OTHER_BINARY:
                result = Other;
                break;
            case SNESHOUT_TEXT:
            case SNESHOUT_BINARY:
                result = SNEShout;
                break;
            case UNKNOWN_BINARY:
                result = Unknown;
                break;
            case ZSNES_W_TEXT:
            case ZSNES_W_BINARY:
                result = ZSNES_W;
                break;
            case SNES9XPP_TEXT:
            case SNES9XPP_BINARY:
                result = Snes9xpp;
                break;
            case SNESGT_BINARY:
            case SNESGT_TEXT:
                result = SNESGT;
                break;
            default:
                result = Unknown; 
        }
        return result;
    }
    
}
