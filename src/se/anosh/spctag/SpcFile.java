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
    private static final int CONTAINS_ID666_TAG = 26;
    private static final int MISSING_ID666_TAG = 27;
    
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
            
            hasId666Tags = containsID666Tags();
            binaryTagFormat = hasBinaryTagFormat();
           
            // emulator offset to use...
            artist = readStuff(Id666Tag.ARTIST_OF_SONG_TEXT_FORMAT_OFFSET, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
            
            if (hasBinaryTagFormat()) {
                artist = readStuff(Id666Tag.ARTIST_OF_SONG_BINARY_FORMAT_OFFSET, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
                setEmulatorUsedToCreateDump(Id666Tag.EMULATOR_BINARY_FORMAT_OFFSET);
            }
            else if (isTextTagFormat()) {
                artist = readStuff(Id666Tag.ARTIST_OF_SONG_TEXT_FORMAT_OFFSET, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
                setEmulatorUsedToCreateDump(Id666Tag.EMULATOR_TEXT_FORMAT_OFFSET);
            }
            else {
                throw new IOException("Something unthinkable occured!");
            }
        raf.close(); // close the file
    }
    
    /**
     * This method is called by readAll()
     * 
     * ZSNES saves in binary format
     * Snes9x saves in text format
     * (as of 2006)
     * 
     * TODO: Fix this method using the japanese ID666-tag spec
     */
    private void setEmulatorUsedToCreateDump(final int offset) throws IOException {
        String emulator = "unknown";
        byte result = readByte(offset);
        switch (result) {
            case 1:
                emulator = "ZSNES";
                break;
            case 2:
                emulator = "Snes9x";
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
    
    /**
     * 
     * @return
     * @throws IOException if offset has invalid value SPC-file.
     */
    private boolean containsID666Tags() throws IOException{
        
        byte tag = readByte(Id666Tag.HEADER_CONTAINS_ID666_TAG_OFFSET);
        if (tag == CONTAINS_ID666_TAG)
            return true;
        else if (tag == MISSING_ID666_TAG)
            return false;
        else
            throw new IOException(Id666Tag.HEADER_CONTAINS_ID666_TAG_OFFSET + " offset does not contain valid value. Is this a SPC file?");
    }
    
    /**
     * 
     * Checks if tag format is text format (as opposed to binary)
     * It is kind of ambigious which format is used since there are
     * no real inidcator in the file format specification.
     * 
     * BUGS:
     * This method only works if the artist field is set...
     * and if the artist name doesn't start with a digit
     * 
     * On the other hand... The only other value that is affected
     * is the single byte that determines the emulator used for creating
     * the dump. And who cares? It's not even properly set in most SPC-files.
     * 
     * @return 
     */
    private boolean hasBinaryTagFormat() throws IOException {
        
        String s = readStuff(Id666Tag.ARTIST_OF_SONG_BINARY_FORMAT_OFFSET,1);
        // If 0xB0 is *NOT* a valid char or *IS* a digit then don't allow it.
        // Sometimes we have valid digits in this offset (if the tag-format is text)
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
