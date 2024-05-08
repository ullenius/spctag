package se.anosh.spctag;

import java.io.IOException;

import org.apache.commons.cli.*;

import org.tinylog.Logger;
import se.anosh.spctag.dao.*;
import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.util.JsonEncoder;
import se.anosh.spctag.util.StringUtil;

/**
 * SPC tag
 * <p>
 * Java command-line tool for reading the ID666 tag from a SNES SPC file.
 * <p>
 * SPC-files are sound files containing ripped chiptune music
 * from Super Nintendo and Super Famicom games.
 */
public final class TagReader {

    private static final String VERSION = "spctag version 2.3.6";
    private static final String ABOUT = "code by A. Ullenius 2019-2023";
    private static final String LICENCE = "Licence: Gnu General Public License - version 3.0 only";
    private static final String TRIBUTE = "spctag is dedicated to my favourite OC remixer: Avien (1986-2004). RIP";

    private static final String VERBOSE = "v";
    private static final String XID6 = "x";

    public static void main(String[] args) {
        Options options = new Options();
        options.addOption(VERBOSE, "verbose", false, "verbose output");
        options.addOption("V", "version", false, "print version");
        options.addOption(XID6, "xid6", false, "print xid6 tags");
        options.addOption("j", "json", false, "output JSON");

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);
            if (cmd.hasOption("V")) {
                printVersionAndCreditsAndExit();
            }

            if (cmd.getArgList().isEmpty()) {
                throw new ParseException("No arguments");
            }

            TagReader tagReader = new TagReader();
            tagReader.go(cmd);
        } catch (ParseException ex) {
            formatter.printHelp("spctag <filename>", options);
            System.exit(-1);
        }
    }

    private static void printVersionAndCreditsAndExit() {
        System.out.println(VERSION);
        System.out.println(ABOUT);
        System.out.println(LICENCE);
        System.out.println(TRIBUTE);
        System.exit(0);
    }

    private void go(final CommandLine cmd) {
        String[] fileNames = cmd.getArgs();

        for (String file : fileNames) {
            try {
                SpcDao spcReader = new SpcFile(file);
                Id666 myFile = spcReader.read();
                final boolean verbose = cmd.hasOption(VERBOSE);
                final boolean printXid6 = cmd.hasOption(XID6);

                if (cmd.hasOption("json")) {
                    printJson(myFile, );

                }

                // NON-JSON OUTPUT--------------------------------------------
                // foobar
                else {
                    if (verbose) {
                        System.out.println("File header: " + myFile.getHeader());
                        String format = myFile.isBinaryTagFormat() ? "Binary" : "Text";
                        System.out.println("Tag format: " + format);
                        //System.out.printf("SPC version minor: %d\n", myFile.getVersion());
                    }
                    System.out.println("Artist: " + myFile.getArtist()); // composer
                    System.out.println("Song title: " + myFile.getSongTitle());
                    System.out.println("Game title: " + myFile.getGameTitle());
                    System.out.println("Name of dumper: " + myFile.getNameOfDumper());
                    System.out.println("Comments: " + myFile.getComments());

                    System.out.println("Date SPC was dumped: " + myFile.dateDumpWasCreated());
                    System.out.println("Emulator used to dump SPC: " + myFile.getEmulatorUsedToCreateDump().getName());
                    if (verbose) {
                        int length = myFile.getLengthSeconds();
                        long fadelength = myFile.getFadeLengthMilliseconds();
                        if (length != 0) {
                            System.out.printf("Length (seconds): %d\n", myFile.getLengthSeconds());
                        }
                        if (fadelength != 0) {
                            System.out.printf("Fade length (milliseconds): %d\n", myFile.getFadeLengthMilliseconds());
                        }
                    }
                    if (verbose || printXid6) {
                        try {
                            Xid6 xid6 = spcReader.readXid6();
                            Xid6Util util = new Xid6Util();
                            util.printTags(xid6);
                        } catch (IOException xid6ex) {
                            Logger.warn("Unable to read xid6 tags", xid6ex);
                        }
                    }
                }

            } catch (IOException ex) {
                Logger.error("I/O error: {}", ex);
                System.exit(1);
            }
        }
    }


    private void printJson() {

        System.out.println("{");
        if (verbose) {
            System.out.printf("t%s,\n", JsonEncoder.toJson("file header", myFile.getHeader()));
            System.out.printf("\t%s,\n", JsonEncoder.toJson("Tag format",
                    myFile.isBinaryTagFormat() ? "Binary" : "Text"));
        }
        System.out.printf("\t%s,\n", JsonEncoder.toJson("artist", myFile.getArtist()));
        System.out.printf("\t%s,\n", JsonEncoder.toJson("Song Title", myFile.getSongTitle()));
        System.out.printf("\t%s,\n", JsonEncoder.toJson("game title", myFile.getGameTitle()));
        System.out.printf("\t%s,\n", JsonEncoder.toJson("name of dumper", myFile.getNameOfDumper()));
        System.out.printf("\t%s,\n", JsonEncoder.toJson("comments", myFile.getComments()));
        System.out.printf("\t%s,\n", JsonEncoder.toJson("date SPC was dumped", myFile.dateDumpWasCreated()));
        if (verbose) {
            System.out.printf("\t%s,\n", JsonEncoder.toJson("lengthSeconds", myFile.getLengthSeconds()));
            System.out.printf("\t%s,\n", JsonEncoder.toJson("\tFade lengthMilliseconds", myFile.getFadeLengthMilliseconds()));
        }
        System.out.printf("\t%s", JsonEncoder.toJson("Emulator used to dump SPC", myFile.getEmulatorUsedToCreateDump().getName()));
        if (verbose || printXid6) {
            try {
                Xid6 xid6 = spcReader.readXid6();
                Xid6Util util = new Xid6Util();
                util.printJson(xid6);
            } catch (IOException xid6ex) {
                Logger.warn("Unable to read xid6 tags", xid6ex);
            }
        } else {
            System.out.printf("\n");
        }
        System.out.println("}");


    }

}
