package se.anosh.spctag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
final class SpcFile {
    
    // version may vary, most recent is 0.31 (?) from 2006
    private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data"; 
    
    private RandomAccessFile raf;
    private final String filename;
    
    private String header;
    private String artist;
    private String songTitle;
    private String gameTitle;
    private String nameOfDumper;
    private String comments;
    private String dateDumpWasCreated;
    private String emulatorUsedToCreateDump;
    
    private boolean hasId666Tags;
    private boolean binaryTagFormat; // boolean isTextTagFormat() returns the opposite of this value

    
    public SpcFile(String filename) throws FileNotFoundException, IOException {
        this.filename = filename;
        raf = new RandomAccessFile(filename,"r"); // read only
        if (!isValidSPCFile())
            throw new IOException("File is missing correct SPC-header. Exiting");
         readAll();
         
         raf.close();
    }

    /**
     * Sets all the fields in the class
     * 
     * @throws FileNotFoundException
     * @throws IOException 
     */
    private void readAll() throws FileNotFoundException, IOException {
        
            header = readStuff(Id666Tag.HEADER_OFFSET, Id666Tag.HEADER_LENGTH).trim(); // removes NULL character
            songTitle = readStuff(Id666Tag.SONG_TITLE_OFFSET, Id666Tag.SONG_TITLE_LENGTH).trim();
            gameTitle = readStuff(Id666Tag.GAME_TITLE_OFFSET, Id666Tag.GAME_TITLE_LENGTH).trim();

            nameOfDumper = readStuff(Id666Tag.NAME_OF_DUMPER_OFFSET, Id666Tag.NAME_OF_DUMPER_LENGTH).trim();
            comments = readStuff(Id666Tag.COMMENTS_OFFSET, Id666Tag.COMMENTS_LENGTH).trim();
            dateDumpWasCreated = (readStuff(Id666Tag.DUMP_DATE_OFFSET, Id666Tag.DUMP_DATE_LENGTH)).trim();
            
            artist = readStuff(Id666Tag.ARTIST_OF_SONG_OFFSET, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
            
            hasId666Tags = containsID666Tags();
            binaryTagFormat = hasBinaryTagFormat();
            
           
            // emulator offset to use...
            int emulatorOffsetToUse = Id666Tag.EMULATOR_OFFSET; // default value, (text format)
            
            System.out.println("kollar 0xB0");
            String s = readStuff(0xB0,2);
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
            
        raf.close(); // close the file
    }
    
    private boolean isValidSPCFile() throws IOException {
        raf.seek(0);
        final String fileHeader = readStuff(Id666Tag.HEADER_OFFSET, Id666Tag.HEADER_LENGTH)
                .trim()
                .substring(0, CORRECT_HEADER.length());
        return (CORRECT_HEADER.equalsIgnoreCase(fileHeader));
    }
    
    
    private boolean containsID666Tags() throws IOException{ // temp, change to catch later. catch and return false
        
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
     * It is kind of ambigious which format is used since there are
     * no real inidcator in the file format specification.
     * 
     * text artist start at: 0xB1
     * binary artist start at: 0xB0
     * 
     * BUGS:
     * This method only works if the artist field is set...
     * and if the artist name doesn't start with a digit
     * 
     * TODO: Replace this with a check to see if 0xB == NULL || 0xB1 == NULL
     * that is ignore 1-letter artists as a workaround
     * 
     * On the other hand... The only other values that are affected
     * is the single byte that determines the emulator used for creating
     * the dump. And who cares? It's not even properly set in most SPC-files in the wild
     * 
     * @return 
     */
    private boolean hasBinaryTagFormat() throws IOException {
        
        String s = readStuff(0xB0,1);
        //if 0xB0 is not a valid char or *IS* a digit then we don't allow it.
        // beause sometimes we have valid digits in this offset (if the tag-format is text)
        if (!Character.isLetter(s.charAt(0)) || Character.isDigit(s.charAt(0))) {
            return false;
        } else {
        return true;
        }
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

    public String getNameOfDumper() {
        return nameOfDumper;
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
    
    public boolean isId666TagsPresent() { // 0-1 grammar vs java convention :(
        return hasId666Tags;
    }
    
    public boolean isBinaryTagFormat() {
        return binaryTagFormat;
    }
    
    public boolean isTextTagFormat() {
        return !isBinaryTagFormat();
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.header);
        hash = 79 * hash + Objects.hashCode(this.artist);
        hash = 79 * hash + Objects.hashCode(this.songTitle);
        hash = 79 * hash + Objects.hashCode(this.gameTitle);
        hash = 79 * hash + Objects.hashCode(this.nameOfDumper);
        hash = 79 * hash + Objects.hashCode(this.comments);
        hash = 79 * hash + Objects.hashCode(this.dateDumpWasCreated);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SpcFile other = (SpcFile) obj;
        if (!Objects.equals(this.header, other.header)) {
            return false;
        }
        if (!Objects.equals(this.artist, other.artist)) {
            return false;
        }
        if (!Objects.equals(this.songTitle, other.songTitle)) {
            return false;
        }
        if (!Objects.equals(this.gameTitle, other.gameTitle)) {
            return false;
        }
        if (!Objects.equals(this.nameOfDumper, other.nameOfDumper)) {
            return false;
        }
        if (!Objects.equals(this.comments, other.comments)) {
            return false;
        }
        if (!Objects.equals(this.dateDumpWasCreated, other.dateDumpWasCreated)) {
            return false;
        }
        return true;
    }
    
    
    
    
}
