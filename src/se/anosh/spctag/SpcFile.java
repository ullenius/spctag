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
            // sets it to default
            int emulatorOffsetToUse = Id666Tag.EMULATOR_OFFSET; // default value, (text format)
            
            System.out.println("kollar 0xB0");
            String s = readStuff(0xB0,1);
            try {
              System.out.println("s = " + s);
            int value = Integer.parseInt(s);
            System.out.println("value = " + value);
            } catch (NumberFormatException ex) {
                //0xB0 är inte en siffra
                // då lagras taggen i binärt format! :)
          artist = readStuff(Id666Tag.ARTIST_OF_SONG_OFFSET_BINARY_FORMAT, Id666Tag.ARTIST_OF_SONG_LENGTH).trim();
          emulatorOffsetToUse = Id666Tag.EMULATOR_OFFSET_BINARY_FORMAT;
            }
            
             // determines the emulator used to dump the file
            String emulator = "unknown";
            byte result = readByte(emulatorOffsetToUse);
            
            //System.out.println("result = " + Byte.valueOf(result)); // debug stuff
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
