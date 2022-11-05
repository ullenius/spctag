package se.anosh.spctag;
import java.io.IOException;
import org.apache.commons.cli.*;

import org.tinylog.Logger;
import se.anosh.spctag.dao.*;
import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;

/**
 *
 * SPC tag
 * 
 * Java command-line tool for reading the ID666 tag from a SNES SPC file.
 * 
 * SPC-files are sound files containing ripped chiptune music 
 * from Super Nintendo and Super Famicom games.
 *
 */
public final class TagReader {
    
    private static final String VERSION ="spctag version 0.3.5";
    private static final String ABOUT = "code by A. Ullenius 2019";
    private static final String LICENCE = "Licence: Gnu General Public License - version 3.0 only";
    private static final String TRIBUTE = "spctag is dedicated to my favourite OC remixer: Avien (1986-2004). RIP";

    private static final String VERBOSE = "v";
    private static final String XID6 = "x";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(VERBOSE, "verbose", false, "verbose output");
        options.addOption("V", "version", false, "print version");
        options.addOption(XID6, "xid6", false, "print xid6 tags");

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

    public void go(final CommandLine cmd) {
        String[] fileNames = cmd.getArgs();

        for (String file : fileNames) {
            try {
                SpcDao spcReader = new SpcFile(file);
                Id666 myFile = spcReader.read();

                if (cmd.hasOption(VERBOSE)) {
                    System.out.println("File header: " + myFile.getHeader());

                    String format = myFile.isBinaryTagFormat() ? "Binary" : "Text";
                    System.out.println("Tag format: " + format);
                }
                System.out.println("Artist: " + myFile.getArtist()); // composer
                System.out.println("Song title: " + myFile.getSongTitle());

                System.out.println("Game title: " + myFile.getGameTitle());
                System.out.println("Name of dumper: " + myFile.getNameOfDumper());
                System.out.println("Comments: " + myFile.getComments());

                System.out.println("Date SPC was dumped: " + myFile.getDateDumpWasCreated());
                System.out.println("Emulator used to dump SPC: " + myFile.getEmulatorUsedToCreateDump().getName());

                if (cmd.hasOption(VERBOSE) || cmd.hasOption(XID6)) {
                    try {
                        Xid6 xid6 = spcReader.readXid6();
                        Xid6Util util = new Xid6Util();
                        util.printTags(xid6);
                    } catch (IOException xid6ex) {
                        Logger.warn("Unable to read xid6 tags", xid6ex);
                    }
                }

            } catch (IOException ex) {
                Logger.error("I/O error", ex);
                System.exit(1);
            }
        } // end of for-each-loop
    }
    
}
