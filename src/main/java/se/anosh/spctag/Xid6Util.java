package se.anosh.spctag;

import org.tinylog.Logger;
import se.anosh.spctag.dao.SpcFile;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.domain.Xid6Tag;
import se.anosh.spctag.util.JsonEncoder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Xid6Util {

    public static void main(String[] args) throws IOException {

        Xid6Util demo = new Xid6Util();
        demo.demo();
    }

    private void demo() throws IOException {
        Path spcDir = Paths.get("/tmp/spcdir");
        var files = listFilesUsingFilesList(spcDir);
        //var files = List.of(Paths.get("/tmp/foobar.spc"));

        Logger.debug("Size of set: {}", files.size());
        for (Path spc : files) {
            var reader = new SpcFile(spc.toString());
            Xid6 xid6 = reader.readXid6();
            printTags(xid6);
        }
    }

    private List<Path> listFilesUsingFilesList(Path dir) throws IOException {
        try (Stream<Path> stream = Files.list(dir)) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.getFileName().toString().toLowerCase().endsWith(".spc"))
                    .collect(Collectors.toList());
        }
    }

    public void printTags(Xid6 xid6) {
        System.out.println("-----------");
        System.out.println("XID6 tags:");
        System.out.println("-----------");
        printLine(Xid6Tag.SONG, xid6.getSong());
        printLine(Xid6Tag.GAME, xid6.getGame());
        printLine(Xid6Tag.ARTIST, xid6.getArtist());
        printLine(Xid6Tag.DUMPER, xid6.getDumper());
        printLine(Xid6Tag.DATE, xid6.getDate() != null ? xid6.getDate().toString() : null);
        printLine(Xid6Tag.EMULATOR, xid6.getEmulator() != null ? xid6.getEmulator().toString() : null);
        printLine(Xid6Tag.COMMENTS, xid6.getComments());
        printLine(Xid6Tag.OST_TITLE, xid6.getOstTitle());
        printLine(Xid6Tag.OST_DISC, xid6.getOstDisc() != null ? Byte.toString(xid6.getOstDisc()) : null);
        printLine(Xid6Tag.OST_TRACK, xid6.getOstTrack() != null ? xid6.getOstTrack().toString() : null);
        printLine(Xid6Tag.PUBLISHER, xid6.getPublisher());
        printLine(Xid6Tag.COPYRIGHT_YEAR, xid6.getYear() != null ? xid6.getYear().toString() : null);
        printLine(Xid6Tag.INTRO, xid6.getIntrolength() != null ? Double.toString(xid6.getIntrolength()) : null);
        printLine(Xid6Tag.LOOP_LENGTH, xid6.getLoopLength() != null ? Long.toString(xid6.getLoopLength()) : null);
        printLine(Xid6Tag.END, xid6.getEndLength() != null ? Long.toString(xid6.getEndLength()) : null);
        printLine(Xid6Tag.FADE, xid6.getFadeLength() != null ? Long.toString(xid6.getFadeLength()) : null);
        if (xid6.hasMutedChannels()) {
            System.out.println("Muted channels: " + xid6.getMutedChannels());
        }
        printLine(Xid6Tag.LOOP_TIMES, xid6.getLoops() != null ? Integer.toString(xid6.getLoops()) : null);
        printLine(Xid6Tag.MIXING, xid6.getMixingLevel() != null ? Long.toString(xid6.getMixingLevel()) : null);
    }

    public void printJson(Xid6 xid6) {

        System.out.print("""
        ,
            "xid6" : {
        """);
        var tags = Stream.of(
                JsonEncoder.toJson(Xid6Tag.SONG, xid6.getSong()),
                JsonEncoder.toJson(Xid6Tag.GAME, xid6.getGame()),
                JsonEncoder.toJson(Xid6Tag.ARTIST, xid6.getArtist()),
                JsonEncoder.toJson(Xid6Tag.DUMPER, xid6.getDumper()),
                JsonEncoder.toJson(Xid6Tag.DATE, xid6.getDate()),
                JsonEncoder.toJson(Xid6Tag.EMULATOR, xid6.getEmulator()),
                JsonEncoder.toJson(Xid6Tag.COMMENTS, xid6.getComments()),
                JsonEncoder.toJson(Xid6Tag.OST_TITLE, xid6.getOstTitle()),
                JsonEncoder.toJson(Xid6Tag.OST_DISC, xid6.getOstDisc()),
                JsonEncoder.toJson(Xid6Tag.OST_TRACK, xid6.getOstTrack()),
                JsonEncoder.toJson(Xid6Tag.PUBLISHER, xid6.getPublisher()),
                JsonEncoder.toJson(Xid6Tag.COPYRIGHT_YEAR, xid6.getYear()),
                JsonEncoder.toJson(Xid6Tag.INTRO, xid6.getIntrolength()),
                JsonEncoder.toJson(Xid6Tag.LOOP_LENGTH, xid6.getLoopLength()),
                JsonEncoder.toJson(Xid6Tag.END, xid6.getEndLength()),
                JsonEncoder.toJson(Xid6Tag.FADE, xid6.getFadeLength()),
                JsonEncoder.toJson(Xid6Tag.MUTED, xid6.getMutedChannels()),
                JsonEncoder.toJson(Xid6Tag.LOOP_TIMES, xid6.getLoops()),
                JsonEncoder.toJson(Xid6Tag.MIXING, xid6.getMixingLevel())
        ).filter(Objects::nonNull).toList();

        final int length = tags.size();
        for (int i = 0; i < length; i++) {
            System.out.printf("\t\t%s", tags.get(i));
            if (i != length - 1) {
                System.out.print(",\n"); // trailing comma
            }
        }
        System.out.print("\n\t}\n");
    }

    private void printLine(Xid6Tag field, String text) {
        if (text != null) {
            System.out.println(field + ": " + text);
        }
    }

}
