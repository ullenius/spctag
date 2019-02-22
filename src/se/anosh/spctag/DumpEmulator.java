/**
 * 
 * This class has a static method that returns
 * the correct emulator type that 
 * 
 * prometheus
 * influxdb
 */
package se.anosh.spctag;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class DumpEmulator {
    
    
    // emulator values for the TEXT tag offset
    private static final int ZSNES_TEXT = 0x31;
    private static final int SNES9X_TEXT = 0x32;
    private static final int ZST2SPC_TEXT = 0x33;
    
    private static final int OTHER_TEXT = 0x34;
    private static final int SNESSHOUT_TEXT = 0x35;
    private static final int ZSNES_W_TEXT = 0x36;
    
    private static final int SNES9XPP = 0x37;
    private static final int SNESGT = 0x38;
    
    // emulator values for the BINARY tag offset
    private static final int UNKNOWN_BINARY = 0x0;
    private static final int ZSNES_BINARY = 0x1;
    private static final int SNES9x_BINARY = 0x2;
    
    private static final int ZST2SPC_BINARY = 0x3;
    private static final int OTHER_BINARY = 0x4;
    private static final int SNESSHOUT_BINARY = 0x5;
    
    private static final int ZSNES_W_BINARY = 0x6;
    private static final int SNES9XPP_BINARY = 0x7;
    private static final int SNESGT_BINARY= 0x8;
   
    public String getEmulatorType(int emulatorId) {
    
        String result;
        switch (emulatorId) {
            case ZSNES_TEXT:
                 result = "ZNES";
                 break;
            case SNES9X_TEXT:
                result = "Snes9x";
                break;
            case ZST2SPC_TEXT:
                result = "ZST2PC";
                break;
            case OTHER_TEXT:
                result = "Other";
                break;
            case SNESSHOUT_TEXT:
                result = "SNEShout";
                break;
            case ZSNES_W_TEXT:
                result ="ZSNES / W";
                break;
            case SNES9XPP:
                result = "Snes9xpp";
                break;
            case SNESGT:
                result = "SNESGT";
                break;
            default:
                result = "unknown";
        } // end of switch-statement
        return result;
    }
}
