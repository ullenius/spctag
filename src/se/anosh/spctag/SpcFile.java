package se.anosh.spctag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
class SpcFile {
    
    // version may vary, most recent is 0.31 (?) from 2006
    private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data"; 
    
    private RandomAccessFile raf;
    private final String filename;
    
    private String header;
    private String artist;
    private String songTitle;
    private String gameTitle;
    private String dumper;
    private String comments;
    private String dateDumpWasCreated;
    private String emulatorUsedToCreateDump;

    
    public SpcFile(String filename) throws FileNotFoundException, IOException {
        this.filename = filename;
        raf = new RandomAccessFile(filename,"r"); // read only
        if (!isValidSPCFile())
            throw new IOException("File is missing correct SPC-header. Exiting");
         readAll();
    }

    private void readAll() throws FileNotFoundException, IOException {
        
            header = readStuff(Id666Tag.HEADER_OFFSET, Id666Tag.HEADER_LENGTH).trim(); // removes NULL character
            songTitle = readStuff(Id666Tag.SONG_TITLE_OFFSET, Id666Tag.SONG_TITLE_LENGTH).trim();
            gameTitle = readStuff(Id666Tag.GAME_TITLE_OFFSET, Id666Tag.GAME_TITLE_LENGTH).trim();

            dumper = readStuff(Id666Tag.NAME_OF_DUMPER_OFFSET, Id666Tag.NAME_OF_DUMPER_LENGTH).trim();
            comments = readStuff(Id666Tag.COMMENTS_OFFSET, Id666Tag.COMMENTS_LENGTH).trim();
            dateDumpWasCreated = (readStuff(Id666Tag.DUMP_DATE_OFFSET, Id666Tag.DUMP_DATE_LENGTH)).trim();
            
            artist = readStuff(Id666Tag.ARTIST_OF_SONG_OFFSET, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
           
            // emulator offset to use...
            int emulatorOffsetToUse = Id666Tag.EMULATOR_OFFSET; // default value, (text format)
            
            System.out.println("kollar 0xB0");
            String s = readStuff(0xB0,1);
            try {
                System.out.println("s = " + s);
                int value = Integer.parseInt(s);
                System.out.println("value = " + value);
            } catch (NumberFormatException ex) {
                System.out.println("kastar exception...");
                //0xB0 if this is not a valid number.
                // NULL-chars cause exception as well. But String.trim() removes them :)
                // then the tag uses binary-format and offsets :)
                artist = readStuff(Id666Tag.ARTIST_OF_SONG_OFFSET_BINARY_FORMAT, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
                if (s.trim().isEmpty()) {
                    emulatorOffsetToUse = Id666Tag.EMULATOR_OFFSET_BINARY_FORMAT;
                    System.out.println("contains null (is empty after trim)");
                }
            }
            
             // determines the emulator used to dump the file
            String emulator = "unknown";
            byte result = readByte(emulatorOffsetToUse);
            
            //System.out.println("result = " + Byte.valueOf(result)); // debug stuff
            switch (result) {
                case 1:
                    emulator = "ZSNES"; // saves in binary format. See SPCFormat_031.txt
                    break;
                case 2:
                    emulator = "Snes9x"; // saves in text format
                    break;
            }
            this.emulatorUsedToCreateDump = emulator;
            
        
    }
    
    private boolean isValidSPCFile() throws IOException {
        
        raf.seek(0);
        final String fileHeader = readStuff(Id666Tag.HEADER_OFFSET, Id666Tag.HEADER_LENGTH)
                .trim()
                .substring(0, CORRECT_HEADER.length());
        return (CORRECT_HEADER.equalsIgnoreCase(fileHeader));
    }
    
    
    public boolean containsID666Tags() throws IOException{ // temp, change to catch later. catch and return false
        
        byte tagValue = readByte(Id666Tag.HEADER_CONTAINS_ID666_TAG_OFFSET);
        
        System.out.println(tagValue);
         // 26 contains id666 tag
         // 27 contains no id666 tag
         // other value, not spc file. throw exception
        if (tagValue == 26)
            return true;
        else if (tagValue == 27)
            return false;
        else
            throw new IOException("Does not contain valid value at offset 0x23. Is this a SPC file?");
        
    }
    
    
    /**
     * 
     * Checks if tag format is text format (as opposed to binary)
     * 
     * text artist start at: 0xB1
     * binary artist start at: 0xB0
     * 
     * BUGS:
     * This method only works if the artist field is set...
     * and if the artist name doesn't start with a digit
     * 
     * @return 
     */
    public boolean hasBinaryTagFormat() throws IOException {
        
       // throw new UnsupportedOperationException("not yet implemented");
        System.out.println("kollar 0xB0");
            String s = readStuff(0xB0,1);
            try {
                System.out.println("a = " + s);
                int value = Integer.parseInt(s); // if the data we read is a digit (or NULL) then it is NOT a binary artist offset being used
                System.out.println("VALUE = " + value);
            } catch (NumberFormatException ex) {
                System.out.println("kastar exception...");
                //0xB0 if this is not a valid number.
                // NULL-chars cause exception as well. But String.trim() removes them :)
                // then the tag uses binary-format and offsets :)
              
                if (s.trim().isEmpty()) { // this statement true if it uses binary format. NULL is trimmed out
                    System.out.println("returning false");
                    return false;
                } 
            }
            System.out.println("returning true");
        return true;
    }
    
    public boolean hasTextTagFormat() throws IOException {
        return (!hasBinaryTagFormat());
    }
    
    
      private String readStuff(int offset, int length) throws IOException {
        
        raf.seek(offset);
        byte[] bytes = new byte[length];
        raf.read(bytes);
        
        return new String(bytes, "ISO-8859-1");
    }
    
    private byte readByte(int offset) throws IOException {
        raf.seek(offset);
              
        byte result = raf.readByte();
        return result;
    }

    public String getFilename() {
        return filename;
    }

    public String getHeader() {
        return header;
    }

    public String getArtist() {
        return artist;
    }

    public String getSongTitle() {
        return songTitle;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getDumper() {
        return dumper;
    }

    public String getComments() {
        return comments;
    }

    public String getDateDumpWasCreated() {
        return dateDumpWasCreated;
    }

    public String getEmulatorUsedToCreateDump() {
        return emulatorUsedToCreateDump;
    }
    
    
}
