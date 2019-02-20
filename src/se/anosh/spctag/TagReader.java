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
    
    public static void main(String[] args) throws FileNotFoundException, IOException {
        
//       System.out.println(args[0]);
//       
//       args = new String[1];
//       args[0] = "/tmp/axelay.spc";
       
        if (args.length < 1) {
            System.out.println("Usage: spctag FILENAME");
            System.exit(0);
        }
        TagReader demo = new TagReader();
        demo.go(args);
    }
    
    public void go(String[] filenames)  {
        
        for (String file : filenames) {
            try {
                SpcFile myFile = new SpcFile(file);
                
                System.out.println("File header: " + myFile.getHeader());
                System.out.println("Artist of song: " + myFile.getArtist()); // composer
                System.out.println("Song title: " + myFile.getSongTitle());
                
                System.out.println("Game title: " + myFile.getGameTitle());
                System.out.println("Name of dumper: " + myFile.getDumper());
                System.out.println("Comments: " + myFile.getComments());
                
                System.out.println("Date SPC was dumped:" + myFile.getDateDumpWasCreated());
                System.out.println("Emulator used to dump SPC: " + myFile.getEmulatorUsedToCreateDump()); // composer
                
            } catch (Exception ex) {
                System.out.println("I/O error");
                ex.printStackTrace();
                System.exit(0);
                
            }
        } // end of for-each-loop
    }
    
}
