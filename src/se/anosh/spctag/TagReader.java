package se.anosh.spctag;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
/**
 *
 * SPC tag 0.1
 * 
 * Java command-line tool for reading the ID666 tag from a SNES SPC file.
 * 
 * SPC-files are sound files containing ripped chiptune music 
 * from Super Nintendo and Super Famicom games. 
 * 
 * They are named after the Sony SPC-700 sound chip created by Ken Kutaragi 
 * (who later became the father of the Playstation).
 * 
 * 
 * @author Anosh D. Ullenius <anosh@anosh.se>
 */
public class TagReader {
    
    private RandomAccessFile raf;
    private Id666Tag tag;
   
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
       // System.out.println(args[0]);
       
       args = new String[1];
       args[0] = "/tmp/axelay.spc";
       
        if (args.length < 1) {
            System.out.println("Usage: spctag FILENAME");
            System.exit(0);
        }
        TagReader demo = new TagReader();
        demo.go(args);
    }
    
    public void go(String[] filenames)  {
       
            try {
                SpcFile myFile = new SpcFile(filenames[0]);
            
            System.out.println("File header: ");
            
            System.out.println("Song title: ");
            
            System.out.println("Game title: ");
            
            System.out.println("Name of dumper: ");
            
            System.out.println("Comments: ");
            
            System.out.println("Date SPC was dumped:");
            
            System.out.println("Artist of song: "); // composer
            
            System.out.println("Emulator used to dump SPC: " + myFile.getEmulatorUsedToCreateDump()); // composer
            
            } catch (Exception ex) {
                System.out.println("I/O error");
                ex.printStackTrace();
                System.exit(0);
                
            }
        }
        
}
