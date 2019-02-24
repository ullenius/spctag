/**
 *
 * This file is part of SPCtag.
 *
 * SPCtag is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 only.
 *
 * SPCtag is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Foobar.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */

package se.anosh.spctag;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.Objects;
import static se.anosh.spctag.Id666Tag.*;
import se.anosh.spctag.emulator.DumpEmulator;
import se.anosh.spctag.emulator.Emulator;

@Deprecated
final public class SpcFile implements Comparable<SpcFile> {
    
    // version may vary, most recent is 0.31 (?) from 2006
    private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data"; 
    private static final int CONTAINS_ID666_TAG = 26;
    private static final int MISSING_ID666_TAG = 27;
    
    // Thanks to Lukasz Wiktor @ stack overflow (2014)
    private static final Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);
    private static final Comparator<SpcFile> SpcFileComparator = Comparator
            .comparing(SpcFile::getGameTitle, nullSafeStringComparator)
            .thenComparing(SpcFile::getArtist, nullSafeStringComparator)
            .thenComparing(SpcFile::getSongTitle, nullSafeStringComparator);
    
    private final RandomAccessFile raf;
    private final String filename;
    
    private String header;
    private String artist;
    private String songTitle;
    private String gameTitle;
    private String nameOfDumper;
    private String comments;
    private String dateDumpWasCreated;
    private Emulator emulatorUsedToCreateDump;
    
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
        
            header = readStuff(HEADER_OFFSET, HEADER_LENGTH).trim(); // removes NULL character
            songTitle = readStuff(SONG_TITLE_OFFSET, SONG_TITLE_LENGTH).trim();
            gameTitle = readStuff(GAME_TITLE_OFFSET, GAME_TITLE_LENGTH).trim();

            nameOfDumper = readStuff(NAME_OF_DUMPER_OFFSET, NAME_OF_DUMPER_LENGTH).trim();
            comments = readStuff(COMMENTS_OFFSET, COMMENTS_LENGTH).trim();
            dateDumpWasCreated = (readStuff(DUMP_DATE_OFFSET, DUMP_DATE_LENGTH)).trim();
            
            hasId666Tags = containsID666Tags();
            binaryTagFormat = hasBinaryTagFormat();
           
            // emulator offset to use...
            artist = readStuff(ARTIST_OF_SONG_TEXT_FORMAT_OFFSET, ARTIST_OF_SONG_LENGTH).trim();
            
            if (hasBinaryTagFormat()) {
                artist = readStuff(ARTIST_OF_SONG_BINARY_FORMAT_OFFSET, ARTIST_OF_SONG_LENGTH).trim();
                setEmulatorUsedToCreateDump(EMULATOR_BINARY_FORMAT_OFFSET);
            }
            else if (isTextTagFormat()) {
                artist = readStuff(ARTIST_OF_SONG_TEXT_FORMAT_OFFSET, ARTIST_OF_SONG_LENGTH).trim();
                setEmulatorUsedToCreateDump(EMULATOR_TEXT_FORMAT_OFFSET);
            }
            else {
                throw new IOException("Something unthinkable occured!");
            }
        raf.close(); // close the file
    }
    
    private void setEmulatorUsedToCreateDump(final int offset) throws IOException {
        
        byte result = readByte(offset);
        this.emulatorUsedToCreateDump = DumpEmulator.getName(result);
    }
    
    private boolean isValidSPCFile() throws IOException {
        raf.seek(0);
        final String fileHeader = readStuff(HEADER_OFFSET, HEADER_LENGTH)
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
        
        byte tag = readByte(HEADER_CONTAINS_ID666_TAG_OFFSET);
        if (tag == CONTAINS_ID666_TAG)
            return true;
        else if (tag == MISSING_ID666_TAG)
            return false;
        else
            throw new IOException(HEADER_CONTAINS_ID666_TAG_OFFSET + " offset does not contain valid value. Is this a SPC file?");
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
        
        String s = readStuff(ARTIST_OF_SONG_BINARY_FORMAT_OFFSET,1);
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
        return emulatorUsedToCreateDump.name();
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

    @Override
    public int compareTo(SpcFile o) {
        
        return SpcFileComparator.compare(this, o);
    }
    
    // These methods are intended for unit testing only
    
    void setArtist(String artist) {
        this.artist = artist;
    }

    void setSongTitle(String songTitle) {
        this.songTitle = songTitle;
    }

    void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    @Override
    public String toString() {
        return "SpcFile{" + "gameTitle=" + gameTitle + ", artist=" + artist+ " gameTitle = " + gameTitle + ", emulatorUsedToCreateDump=" + emulatorUsedToCreateDump + ", hasId666Tags=" + hasId666Tags + ", binaryTagFormat=" + binaryTagFormat + '}';
    }
    
    
    
}
