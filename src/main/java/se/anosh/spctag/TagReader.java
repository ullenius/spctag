package se.anosh.spctag;

import java.io.IOException;

import org.apache.commons.cli.*;

import org.tinylog.Logger;
import se.anosh.spctag.dao.*;
import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.util.JsonEncoder;

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
    private static final String FILE_HEADER = "File header";
    private static final String TAG_FORMAT = "Tag format";
    private static final String ARTIST = "Artist";
    private static final String SONG_TITLE = "Song title";
    private static final String GAME_TITLE = "Game title";
    private static final String NAME_OF_DUMPER = "Dumped by";
    private static final String COMMENTS = "Comments";
    private static final String DATE_SPC_WAS_DUMPED = "Date SPC was dumped";
    private static final String EMULATOR_USED_TO_DUMP_SPC = "Emulator used to dump SPC";
    private static final String LENGTH_SECONDS = "Length (seconds)";
    private static final String FADE_LENGTH_MILLISECONDS = "Fade length (milliseconds)";
    private static final String BINARY = "Binary";
    private static final String TEXT = "Text";

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
                    System.out.println("{");
                    if (verbose) {
                        System.out.printf("\t%s,\n", JsonEncoder.toJson(FILE_HEADER, myFile.getHeader()));
                        System.out.printf("\t%s,\n", JsonEncoder.toJson(TAG_FORMAT,
                                myFile.isBinaryTagFormat() ? BINARY : TEXT));
                    }
                    System.out.printf("\t%s,\n", JsonEncoder.toJson(ARTIST, myFile.getArtist()));
                    System.out.printf("\t%s,\n", JsonEncoder.toJson(SONG_TITLE, myFile.getSongTitle()));
                    System.out.printf("\t%s,\n", JsonEncoder.toJson(GAME_TITLE, myFile.getGameTitle()));
                    System.out.printf("\t%s,\n", JsonEncoder.toJson(NAME_OF_DUMPER, myFile.getNameOfDumper()));
                    System.out.printf("\t%s,\n", JsonEncoder.toJson(COMMENTS, myFile.getComments()));
                    System.out.printf("\t%s,\n", JsonEncoder.toJson(DATE_SPC_WAS_DUMPED, myFile.dateDumpWasCreated()));
                    if (verbose) {
                        System.out.printf("\t%s,\n", JsonEncoder.toJson(LENGTH_SECONDS, myFile.getLengthSeconds()));
                        System.out.printf("\t%s,\n", JsonEncoder.toJson(FADE_LENGTH_MILLISECONDS, myFile.getFadeLengthMilliseconds()));
                        }
                    System.out.printf("\t%s", JsonEncoder.toJson(EMULATOR_USED_TO_DUMP_SPC, myFile.getEmulatorUsedToCreateDump().getName()));
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

                // NON-JSON OUTPUT--------------------------------------------
                else {
                    if (verbose) {
                        System.out.printf("%s: %s\n", FILE_HEADER, myFile.getHeader());
                        String format = myFile.isBinaryTagFormat() ? BINARY : TEXT;
                        System.out.printf("%s: %s\n", TAG_FORMAT, format);
                        //System.out.printf("SPC version minor: %d\n", myFile.getVersion());
                    }
                    System.out.printf("%s: %s\n", ARTIST, myFile.getArtist()); // composer
                    System.out.printf("%s: %s\n", SONG_TITLE, myFile.getSongTitle());
                    System.out.printf("%s: %s\n", GAME_TITLE, myFile.getGameTitle());
                    System.out.printf("%s: %s\n", NAME_OF_DUMPER, myFile.getNameOfDumper());
                    System.out.printf("%s: %s\n", COMMENTS, myFile.getComments());

                    System.out.printf("%s: %s\n", DATE_SPC_WAS_DUMPED, myFile.dateDumpWasCreated());
                    System.out.printf("%s: %s\n", EMULATOR_USED_TO_DUMP_SPC, myFile.getEmulatorUsedToCreateDump().getName());
                    if (verbose) {
                        int length = myFile.getLengthSeconds();
                        long fadelength = myFile.getFadeLengthMilliseconds();
                        if (length != 0) {
                            System.out.printf("%s: %d\n", LENGTH_SECONDS, myFile.getLengthSeconds());
                        }
                        if (fadelength != 0) {
                            System.out.printf("%s: %d\n", FADE_LENGTH_MILLISECONDS, myFile.getFadeLengthMilliseconds());
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

}
