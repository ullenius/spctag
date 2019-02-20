/**
 * 
 * ID666 tag offsets used in SPC-files (Sony SPC700) sound chip
 * used in the Super Nintendo / Super Famicom
 * 
 */
package se.anosh.spctag;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public final class Id666Tag {
    
    public Id666Tag() {
    }
    
    public static final int HEADER_OFFSET = 0;
    public static final int HEADER_LENGTH = 33;

    public static final int SONG_TITLE_OFFSET = 0x2E;
    public static final int SONG_TITLE_LENGTH = 32;
    
    public static final int GAME_TITLE_OFFSET = 0x4E;
    public static final int GAME_TITLE_LENGTH = 32;
    
    public static final int NAME_OF_DUMPER_OFFSET = 0x6E;
    public static final int NAME_OF_DUMPER_LENGTH = 16;
    
    public static final int COMMENTS_OFFSET = 0x7E;
    public static final int COMMENTS_LENGTH = 32;
    
    public static final int DUMP_DATE_OFFSET = 0x9E;
    public static final int DUMP_DATE_LENGTH = 11;
    
    public static final int ARTIST_OF_SONG_OFFSET = 0xb1;
    public static final int ARTIST_OF_SONG_LENGTH = 32;
    
    /**
     * Emulator used:
     * 0 = unknown
     * 1 = ZNES
     * 2 = Snes9x
     */
    public static final int EMULATOR_OFFSET = 0xD2;
    public static final int EMULATOR_LENGTH = 1;
       
}
