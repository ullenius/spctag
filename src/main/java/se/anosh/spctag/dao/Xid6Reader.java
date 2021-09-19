package se.anosh.spctag.dao;

import org.tinylog.Logger;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.domain.Xid6Tag;

import javax.transaction.xa.Xid;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.toHexString;

public class Xid6Reader {

    private static final Map<Byte, Id> mappningar = new HashMap<>();
    private static final double INTRO_LENGTH_DIVISOR = 64_0000.0;
    private static final long XID6_OFFSET = 0x10200L;

    static {
        mappningar.put((byte) 0x1, new Id(Xid6Tag.SONG, Type.TEXT));
        mappningar.put((byte) 0x2, new Id(Xid6Tag.GAME, Type.TEXT));
        mappningar.put((byte) 0x3, new Id(Xid6Tag.ARTIST, Type.TEXT));
        mappningar.put((byte) 0x4, new Id(Xid6Tag.DUMPER, Type.TEXT));


        mappningar.put((byte) 0x5, new Id("Date song was dumped", Type.NUMBER));
        mappningar.put((byte) 0x6, new Id("Emulator used", Type.DATA));
        mappningar.put((byte) 0x7, new Id("Comments", Type.TEXT));
        mappningar.put((byte) 0x10, new Id("Official Soundtrack Title", Type.TEXT));
        mappningar.put((byte) 0x11, new Id("OST disc", Type.DATA));
        mappningar.put((byte) 0x12, new Id("OST track", Type.OST));
        mappningar.put((byte) 0x13, new Id("Publisher's name", Type.TEXT));
        mappningar.put((byte) 0x14, new Id("Copyright year", Type.YEAR));
        // song info stuff
        mappningar.put((byte) 0x30, new Id("Introduction length", Type.INTRO));
        mappningar.put((byte) 0x31, new Id("Loop length", Type.NUMBER));
        mappningar.put((byte) 0x32, new Id("End length", Type.NUMBER));
        mappningar.put((byte) 0x33, new Id("Fade length", Type.NUMBER));
        mappningar.put((byte) 0x34, new Id("Muted voices (1 bit for each muted voice)", Type.MUTED));
        mappningar.put((byte) 0x35, new Id("Number of times to loop", Type.DATA));
        mappningar.put((byte) 0x36, new Id("Mixing (preamp) level", Type.NUMBER));
    }

    private Xid6 xid6 = null;

    final BiConsumer<Id, byte[]> year = (id, data) -> {
        xid6.setYear(toShort(data));
        System.out.println("Year: " + xid6.getYear());
    };
    final BiConsumer<Id, byte[]> muted = (id, data) -> {
        xid6.setMutedVoices(data[0]);
        System.out.print(id.name + ": ");
        final byte muted = data[0];
        for (int i = 0; i < 8; i++) {
            System.out.print(((1 << i) & muted) != 0 ? 0 : 1);
        }
        System.out.println();
    };
    final BiConsumer<Id, byte[]> ost = (id, data) -> {
        byte hibyte = data[0];
        byte lobyte = data[1];
        boolean hasHiByte = hibyte != (byte) 0;
        Logger.debug("Has hi byte: {}", hasHiByte);

        System.out.println("Upper byte (char): " + ((hasHiByte && isAscii(hibyte))
                ? (char) hibyte
                : "0"));
        System.out.println("Lower byte (number): " + lobyte);
        if (lobyte < 0 || lobyte > 99) {
            throw new IllegalStateException("track no is invalid: " + lobyte);
        }
    };

    private static boolean isAscii(int ch) {
        return ch >= (int)'a' && ch <= (int)'z'
                || ch >= (int) 'A' && ch <= (int) 'Z';
    }

    final BiConsumer<Id, byte[]> oneByteData = (id, data) -> System.out.println(id.name + ": " + data[0]);

    private final Map<Type, BiConsumer<Id, byte[]>> mappedBehaviourDataStoredInHeader = Map.of(
            Type.OST, ost,
            Type.YEAR, year,
            Type.MUTED, muted,
            Type.DATA, oneByteData
    );

    public static void main(String[] args) throws IOException {
        Xid6Reader demo = new Xid6Reader();
        demo.mappedVersion();
    }

    public List<Path> listFilesUsingFilesList(String dir) throws IOException {
        try (Stream<Path> stream = Files.list(Paths.get(dir))) {
            return stream
                    .filter(file -> !Files.isDirectory(file))
                    .filter(file -> file.getFileName().toString().toLowerCase().endsWith(".spc"))
                    .collect(Collectors.toList());
        }
    }

    private void mappedVersion() throws IOException {
        var files = listFilesUsingFilesList("");
        Logger.debug("Size of set: {}", files.size());
        for (Path spc : files) {
            System.out.println("-----------");
            System.out.println("Filename: " + spc.getFileName());
            parseXid6(spc);
        }
    }

    private void parseXid6(Path spc) throws IOException {
        xid6 = new Xid6();

        final long fileSize = Files.size(spc);
        final long xid6Size = fileSize - XID6_OFFSET;
        final long xid6SizeMinusHeader = xid6Size - 8; // size of header
        if (fileSize <= XID6_OFFSET) {
            throw new IllegalArgumentException("File too small. Does not contain xid6");
        }

        var fileChannel = FileChannel.open(spc, StandardOpenOption.READ);
        fileChannel.position(XID6_OFFSET);

        var buffer = fileChannel.map(
                FileChannel.MapMode.READ_ONLY, XID6_OFFSET, xid6Size
        );
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        Logger.debug("Position: {]", buffer.position());
        Logger.debug("Limit: {]", buffer.limit());

        byte[] magic = new byte[4];
        buffer.get(magic);
        ChunkHeader header = new ChunkHeader(magic, buffer.getInt());
        // workaround if header is broken and actual filesize is less than chunk size in header
        final int chunkSize = (header.chunkSize > xid6SizeMinusHeader) ? (int) xid6SizeMinusHeader : header.chunkSize;
        Logger.debug("Chunk size: {}", chunkSize);
        Logger.debug("Current pos: {}", buffer.position());

        byte[] subChunkArr = new byte[chunkSize]; // chunkSize exclusive header
        buffer.get(subChunkArr); // contains all sub-chunks, including sub-chunk headers
        var subChunks = ByteBuffer.wrap(subChunkArr).order(ByteOrder.LITTLE_ENDIAN);

        Logger.debug("Subchunk contents");
        Logger.debug("-----------");
        while (subChunks.position() < subChunks.limit()) {
            Logger.debug("Subchunk header");
            Logger.debug("Current pos: {}", subChunks.position());

            byte id = subChunks.get();
            if (!mappningar.containsKey(id)) {
                break;
            }
            Id mappatId = mappningar.get(id);
            Type type = mappatId.type;
            boolean dataStoredInHeader = subChunks.get() == 0 || type == Type.DATA; // workaround for broken type byte, always read from header

            Logger.debug("Id: 0x{}", toHexString(id));
            Logger.debug("Mapped id: {}", mappatId);
            Logger.debug("Data stored in header: {}", dataStoredInHeader);

            int size = (dataStoredInHeader)
                    ? mappatId.type.size()
                    : subChunks.getShort(); // changes current offset
            Logger.debug("Size: {}", size);

            if (dataStoredInHeader) { // max 2 bytes
                if (size > 2) {
                    throw new IllegalStateException("Data stored in header. Yet size is larger than 2 bytes");
                }
                byte[] data = new byte[2]; // always allocate 2 bytes
                subChunks.get(data);

                var func = mappedBehaviourDataStoredInHeader.get(type);
                func.accept(mappatId, data); // print info

            } else {
                switch (type) {
                    case TEXT:
                        // peek at last byte, workaround for broken tags
                        if ((size-1) % 4 == 0) {
                            final int peekPos = size - 1;
                            final int oldPos = subChunks.position();
                            byte peek = subChunks.get(oldPos + peekPos);
                            Logger.debug("Peek byte: {}", peek);
                            if (mappningar.containsKey(peek)) {
                                size--;
                            }
                        }
                        int bufsize = size;
                        while (bufsize % 4 != 0) {
                            bufsize++;
                            Logger.debug("Increasing bufsize: {}", bufsize);
                        }
                        byte buf[] = new byte[bufsize];
                        subChunks.get(buf);
                        String text = new String(buf, StandardCharsets.UTF_8).trim();
                        System.out.println(mappatId.name + ": " + text);
                        break;
                    case INTRO:
                        if (type.size() == Integer.BYTES) {
                            xid6.setIntro(subChunks.getInt());
                            System.out.println("Intro length (seconds): " + xid6.getIntrolength());
                        }
                        break;
                    case NUMBER:
                        if (type.size() == Integer.BYTES) {
                            xid6.setNumber(mappatId, subChunks.getInt());
                            int num = subChunks.getInt();
                            System.out.println(mappatId.name + " : " +num);
                        }
                        break;
                    default:
                        throw new IllegalStateException("no mapping for type: " + type);
                }
            }
        }
        fileChannel.close();
    }

    private static class ChunkHeader {
        private static final String MAGIC_NUMBER = "xid6";
        private final int chunkSize;
        ChunkHeader(byte[] magicNumber, int size) {
            String actual = new String(magicNumber, StandardCharsets.UTF_8);
            if (!MAGIC_NUMBER.contentEquals(actual)) {
                throw new IllegalArgumentException("invalid magic number");
            }
            this.chunkSize = size;
        }
    }


    private short toShort(byte buf[]) { // little endian
        return (short) (
                (((buf[1] & 0xFF) << 8) | (buf[0]) & 0xFF));
    }

    private static final class Id {
        Id(Xid6Tag tag, Type type) {
            this.tag = tag;
            this.type = type;
        }

        final Xid6Tag tag;
        final Type type;

        @Override
        public String toString() {
            return "Id{" +
                    "name='" + tag + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

}
