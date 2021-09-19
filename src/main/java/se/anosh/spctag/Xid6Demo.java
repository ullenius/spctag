package se.anosh.spctag;

import org.tinylog.Logger;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.domain.Xid6Tag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Xid6Demo {

    public static void main(String[] args) throws IOException {

        Xid6Demo demo = new Xid6Demo();
        demo.mappedVersion();
    }

    private void mappedVersion() throws IOException {
        Path spcDir = Paths.get("/tmp/spcdir");
        var files = listFilesUsingFilesList(spcDir);
        //var files = List.of(Paths.get("/tmp/foobar.spc"));

        Logger.debug("Size of set: {}", files.size());
        for (Path spc : files) {

            var reader = new SpcFile(spc.toString());
            Xid6 xid6 = reader.readXid6();

            System.out.println("-----------");
            System.out.println("Filename: " + spc.getFileName());
            //parseXid6(spc);
            printLine(Xid6Tag.SONG, xid6.getSong());
            printLine(Xid6Tag.GAME, xid6.getGame());
            printLine(Xid6Tag.ARTIST, xid6.getArtist());
            printLine(Xid6Tag.DUMPER, xid6.getDumper());
            printLine(Xid6Tag.DATE, xid6.getDate() != 0 ? Integer.toString(xid6.getDate()) : null);
            printLine(Xid6Tag.DATE, xid6.getDate() != 0 ? Integer.toString(xid6.getDate()) : null);
            printLine(Xid6Tag.EMULATOR, xid6.getEmulator() != null ? Byte.toString(xid6.getEmulator()) : null);
            printLine(Xid6Tag.COMMENTS, xid6.getComments());
            printLine(Xid6Tag.OST_TITLE, xid6.getOstTitle());
            printLine(Xid6Tag.OST_DISC, xid6.getOstDisc() != null ? Byte.toString(xid6.getOstDisc()) : null);
            printLine(Xid6Tag.OST_TRACK, xid6.getOstTrack() != null ? xid6.getOstTrack().toString() : null);
            printLine(Xid6Tag.PUBLISHER, xid6.getPublisher());
            printLine(Xid6Tag.COPYRIGHT_YEAR, xid6.getYear() != null ? xid6.getYear().toString() : null);
            printLine(Xid6Tag.INTRO, xid6.getIntrolength() != null ? Double.toString(xid6.getIntrolength()) : null);
            printLine(Xid6Tag.LOOP_LENGTH, xid6.getLoopLength() != null ? Integer.toString(xid6.getLoopLength()) : null);
            printLine(Xid6Tag.END, xid6.getEndLength() != null ? Integer.toString(xid6.getEndLength()) : null);
            printLine(Xid6Tag.FADE, xid6.getFadeLength() != null ? Integer.toString(xid6.getFadeLength()) : null);
            if (xid6.hasMutedVoices()) {
                System.out.print("Muted voices: ");
                xid6.printMutedVoices();
            }
            printLine(Xid6Tag.LOOP_TIMES, xid6.getLoops() != null ? Integer.toString(xid6.getLoops()) : null);
            printLine(Xid6Tag.MIXING, xid6.getMixingLevel() != null ? Integer.toString(xid6.getMixingLevel()) : null);
        }
    }

    private void printLine(Xid6Tag field, String text) {
        if (text != null) {
            System.out.println(field + ": " + text);
        }
    }

    public List<Path> listFilesUsingFilesList(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.getFileName().toString().toLowerCase().endsWith(".spc"))
                    .collect(Collectors.toList());
        }
    }
}
