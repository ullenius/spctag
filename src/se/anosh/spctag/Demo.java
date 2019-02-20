package se.anosh.spctag;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


/**
 *
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class Demo {
    
    private RandomAccessFile raf;
    private Id666Tag tag;
   
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
        System.out.println(args[0]);
        
        if (args.length < 1) {
            System.out.println("Usage: spicy FILENAME");
            System.exit(0);
        }
        Demo demo = new Demo();
        demo.go(args);
    }
    
    public void go(String[] filenames) throws FileNotFoundException, IOException  {
       
        
        for (String file : filenames) {
            System.out.println("File name: " + file);
            
            raf = new RandomAccessFile(filenames[0], "r");
            
            System.out.print("File header: ");
            System.out.println(readStuff(tag.HEADER_OFFSET, tag.HEADER_LENGTH));
            
            System.out.print("Song title: ");
            System.out.println(readStuff(tag.SONG_TITLE_OFFSET, tag.SONG_TITLE_LENGTH));
            
            System.out.print("Game title: ");
            System.out.println(readStuff(tag.GAME_TITLE_OFFSET, tag.GAME_TITLE_LENGTH));
            
            System.out.print("Name of dumper: ");
            System.out.println(readStuff(tag.NAME_OF_DUMPER_OFFSET, tag.NAME_OF_DUMPER_LENGTH));
            
            System.out.print("Comments: ");
            System.out.println(readStuff(tag.COMMENTS_OFFSET, tag.COMMENTS_LENGTH));
            
            System.out.print("Date SPC was dumped:");
            System.out.println(readStuff(tag.DUMP_DATE_OFFSET, tag.DUMP_DATE_LENGTH));
            
            System.out.print("Artist of song: "); // composer
            System.out.println(readStuff(tag.ARTIST_OF_SONG_OFFSET, tag.ARTIST_OF_SONG_LENGTH));
            
            String emulator = "unknown";
            byte result = readByte(tag.EMULATOR_OFFSET);
            //System.out.println("result = " + result);
            switch (result) {
                case 1:
                    emulator = "ZSNES";
                    break;
                case 2:
                    emulator = "Snes9x";
                    break;
            }
            System.out.println("Emulator used to dump SPC: " + emulator); // composer
        }
        
    }
    
    private String readStuff(int offset, int length) throws IOException {
        
        raf.seek(offset);
        byte[] bytes = new byte[length];
        raf.read(bytes);
        
        return new String(bytes);
    }
    
    private byte readByte(int offset) throws IOException {
        raf.seek(offset);
              
        byte result = raf.readByte();
        return result;
    }
    
}
