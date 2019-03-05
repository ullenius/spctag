package se.anosh.spctag;
import java.io.IOException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import se.anosh.spctag.dao.Id666;
import se.anosh.spctag.dao.SpcFileImplementation;
import se.anosh.spctag.service.SpcManager;
import se.anosh.spctag.service.SpcService;
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
 * code written in February 2019
 */
public class TagReader {
    
    private static final String VERSION ="spctag version 0.1";
    private static final String ABOUT = "code by A. Ullenius 2019";
    private static final String LICENCE = "Licence: Gnu General Public License - version 3.0 only";
    private static final String TRIBUTE = "spctag is dedicated to my favourite OC remixer: Chris 'Avien' Powell (1986-2004). RIP";
    
    public static void main(String[] args) {
        
        Options options = new Options();
        options.addOption("v", "verbose", false, "verbose output");
        options.addOption("V", "version", false, "print version");
        
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        try {
            CommandLine cmd = parser.parse(options, args);
            
            if (cmd.hasOption("V"))
                printVersionAndCreditsAndExit();
            
            if (cmd.getArgList().isEmpty())
                    throw new ParseException("No arguments");
            
            TagReader demo = new TagReader();
            demo.go(cmd);
        } catch (ParseException ex) {
            formatter.printHelp("spctag <filename>", options);
            System.exit(0);
        }
        
    }
    
    private static void printVersionAndCreditsAndExit() {
        
        System.out.println(VERSION);
        System.out.println(ABOUT);
        System.out.println(LICENCE);
        System.out.println(TRIBUTE);
        System.exit(0);
    }
    
    public void go(final CommandLine cmd)  {
        
        String[] fileNames = cmd.getArgs();
        
        for (String file : fileNames) {
            try {
            	
            	SpcService service = new SpcManager(new SpcFileImplementation(file));
            	Id666 myFile = service.read();
            	
            	if (cmd.hasOption("v")) { // verbose output
            		System.out.println("File header: " + myFile.getHeader());

            		String format = myFile.isBinaryTagFormat() ? "Binary" : "Text"; // ternary operator
            		System.out.println("Tag format: " + format);
            	}
                System.out.println("Artist: " + myFile.getArtist()); // composer
                System.out.println("Song title: " + myFile.getSongTitle());
                
                System.out.println("Game title: " + myFile.getGameTitle());
                System.out.println("Name of dumper: " + myFile.getNameOfDumper());
                System.out.println("Comments: " + myFile.getComments());
                
                System.out.println("Date SPC was dumped:" + myFile.getDateDumpWasCreated());
                System.out.println("Emulator used to dump SPC: " + myFile.getEmulatorUsedToCreateDump().getName());
                
            } catch (IOException ex) {
                System.out.println("I/O error");
                //ex.printStackTrace();
                System.exit(0);
            }
        } // end of for-each-loop
    }
    
}
