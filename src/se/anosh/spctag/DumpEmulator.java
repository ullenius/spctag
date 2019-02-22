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
    
    private static final int ZSNES_TEXT = 0x31;
    private static final int SNES9X_TEXT = 0x32;
    private static final int ZST2SPC_TEXT = 0x33;
    private static final int OTHER_TEXT = 0x34;
    private static final int SNESSHOUT_TEXT = 0x35;
    private static final int ZSNES_W_TEXT = 0x36;
    private static final int SNES9XPP = 0x37;
    private static final int SNESGT = 0x38;
    
    private static final int UNKNOWN_BINARY = 0x0;
    private static final int ZSNES_BINARY = 0x01;
    private static final int SNES9x_BINARY = 0x02;
    private static final int ZST2SPC_BINARY = 0x03;
    private static final int OTHER_BINARY = 0x04;
    private static final int ZSNES_BINARY = 0x01;
    private static final int ZSNES_BINARY = 0x01;
    private static final int ZSNES_BINARY = 0x01;
    private static final int ZSNES_BINARY = 0x01;
   
            
//            ID 666 binary format
//Emulator type: 
//0x00 = Unknown, 
//0x01 = ZSNES,  
//0x02 = Snes9x, 
//0x03 = ZST2SPC, 
//0x04 = Other,
//0x05 = SNEShout, 
//0x06 = ZSNES / W, 
//0x07 = Snes9xpp, 
//0x08 = SNESGT


            
            
    
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
        }
        
            
            
//id666 text tag format
//Emulator type: 0x30 = Unknown,
//0x31 = ZSNES,
//0x32 = Snes9x,
//0x33 = ZST2SPC,
//0x34 = Other, 
//0x35 = SNEShout, 
//0x36 = ZSNES / W, 
//0x37 = Snes9xpp,
//0x38 = SNESGT

            
            
        }
        
    
    }
    
    
    
}
