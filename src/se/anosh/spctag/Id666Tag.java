package se.anosh.spctag;

/**
 *
 * ID666 tag offsets used in SPC-files (Sony SPC-700 sound chip)
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public final class Id666Tag {
    
    private Id666Tag() {
        throw new AssertionError(); //prevent instantiation
    }
    
    public static final int HEADER_CONTAINS_ID666_TAG_OFFSET = 0x23; // 1 byte
    
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
    
    public static final int ARTIST_OF_SONG_TEXT_FORMAT_OFFSET = 0xB1; // text format
    public static final int ARTIST_OF_SONG_BINARY_FORMAT_OFFSET = 0xB0;
    public static final int ARTIST_OF_SONG_LENGTH = 32;
    
    /**
     * Emulator used:
     * 0 = unknown
     * 1 = ZNES
     * 2 = Snes9x
     */
    public static final int EMULATOR_TEXT_FORMAT_OFFSET = 0xD2;
    public static final int EMULATOR_BINARY_FORMAT_OFFSET = 0xD1;
    
    public static final int EMULATOR_LENGTH = 1;
    
       
}
