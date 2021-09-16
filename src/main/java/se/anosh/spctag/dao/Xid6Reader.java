package se.anosh.spctag.dao;

import org.tinylog.Logger;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.toHexString;

public class Xid6Reader {

    private static final Map<Byte, Id> mappningar = new HashMap<>();
    private static final double INTRO_LENGTH_DIVISOR = 64_0000.0;

    static {
        mappningar.put((byte) 0x1, new Id("Song name", Type.TEXT)); // FIXME byt så att den skriver ut rätt rubriker
        mappningar.put((byte) 0x2, new Id("Game name", Type.TEXT));
        mappningar.put((byte) 0x3, new Id("Artist's name", Type.TEXT));
        mappningar.put((byte) 0x4, new Id("Dumper's name", Type.TEXT));
        mappningar.put((byte) 0x5, new Id("Date song was stored", Type.NUMBER));
        mappningar.put((byte) 0x6, new Id("Emulator used", Type.DATA));
        mappningar.put((byte) 0x7, new Id("Comments", Type.TEXT));
        mappningar.put((byte) 0x10, new Id("Official Soundtrack Title", Type.TEXT));
        mappningar.put((byte) 0x11, new Id("OST disc", Type.DATA));
        mappningar.put((byte) 0x12, new Id("OST Track", Type.OST));
        mappningar.put((byte) 0x13, new Id("Publisher's name", Type.TEXT));
        mappningar.put((byte) 0x14, new Id("Copyright year", Type.YEAR));
        // song info stuff
        mappningar.put((byte) 0x30, new Id("Introduction length", Type.INTRO));
        mappningar.put((byte) 0x31, new Id("Loop length", Type.NUMBER));
        mappningar.put((byte) 0x32, new Id("End length", Type.NUMBER));
        mappningar.put((byte) 0x33, new Id("Fade length", Type.NUMBER));
        mappningar.put((byte) 0x34, new Id("Muted voices (1 bit for each muted voice)", Type.MUTED)); // print bits
        mappningar.put((byte) 0x35, new Id("Number of times to loop", Type.DATA));
        mappningar.put((byte) 0x36, new Id("Mixing (preamp) level", Type.NUMBER));
    }

    final BiConsumer<Id, byte[]> year = (id, data) -> System.out.println("Year: " + toShort(data));
    final BiConsumer<Id, byte[]> muted = (id, data) -> {
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

        System.out.println("Upper byte (char): " + ((hasHiByte) // FIXME check valid ascii
                ? Character.getNumericValue(Byte.toUnsignedInt(hibyte))
                : "0"));
        System.out.println("Lower byte (number): " + lobyte);
        if (lobyte < 0 || lobyte > 99) {
            throw new IllegalStateException("track no is invalid: " + lobyte);
        }
    };
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
   
        Logger.debug("Size of set: {}", files.size());
        List<Byte> unknownMappings = new LinkedList<>();
        List<String> unknownMappingfiles = new LinkedList<>();

        final long offset = 0x10200;

        for (Path spc : files) {

            System.out.println("-----------");
            System.out.println("Filename: " + spc.getFileName());

            final long fileSize = Files.size(spc);
            final long xid6Size = fileSize - offset;
            final long xid6SizeMinusHeader = xid6Size - 8; // size of header

            if (fileSize <= offset) {
                throw new IllegalArgumentException("File too small. Does not contain xid6");
            }

            var fileChannel = FileChannel.open(spc, StandardOpenOption.READ);
            fileChannel.position(offset);

            var buffer = fileChannel.map(
                    FileChannel.MapMode.READ_ONLY, offset, xid6Size
            );
            buffer.order(ByteOrder.LITTLE_ENDIAN);
            Logger.debug("Position: {]", buffer.position());
            Logger.debug("Limit: {]", buffer.limit());

            byte[] magic = new byte[4];
            buffer.get(magic);
            final String magicNumber = "xid6";
            Logger.debug("Magic number: {}", new String(magic, StandardCharsets.UTF_8));
            if (!magicNumber.contentEquals(new String(magic, StandardCharsets.UTF_8))) {
                throw new IllegalArgumentException("invalid magic number");
            }

            // workaround if header is broken and actual filesize is less than chunk size in header
            int tmp = buffer.getInt();
            final int chunkSize = (tmp > xid6SizeMinusHeader) ? (int) xid6SizeMinusHeader : tmp;

            Logger.debug("Chunk size: {}", chunkSize);
            Logger.debug("Current pos: {}", buffer.position());

            byte[] subChunkArr = new byte[chunkSize]; // chunkSize exclusive header
            buffer.get(subChunkArr); // contains all sub-chunks, including sub-chunk headers
            var subChunks = ByteBuffer.wrap(subChunkArr).order(ByteOrder.LITTLE_ENDIAN); // FIXME felaktigt namn, är hela subchunken

            Logger.debug("Subchunk contents");
            Logger.debug("-----------");
            while (subChunks.position() < subChunks.limit()) {
                Logger.debug("Subchunk header");
                Logger.debug("Current pos: {}", subChunks.position());

                byte id = subChunks.get();
                if (!mappningar.containsKey(id)) {
                    unknownMappings.add(id); // debug
                    unknownMappingfiles.add(spc.getFileName().toString()); // debug
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
                            if (type.size == Integer.BYTES) {
                                int num = subChunks.getInt();
                                Logger.debug("Number: " + num);
                                System.out.println("Intro length (seconds): " + num / INTRO_LENGTH_DIVISOR);
                            }
                            break;
                        case NUMBER:
                            if (type.size == Integer.BYTES) {
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
        Logger.debug("Unknown mappings: {}", unknownMappings);
        Collections.sort(unknownMappingfiles);
        Logger.debug("Files with unkown mappings {}", unknownMappingfiles);
    }

    private short toShort(byte buf[]) { // little endian
        return (short) (
                (((buf[1] & 0xFF) << 8) | (buf[0]) & 0xFF));
    }

    private enum Type {
        TEXT(256),
        OST(2),
        DATA(1),
        NUMBER(4),
        INTRO(4),
        MUTED(1),
        YEAR(2);

        private final int size;

        Type(int size) {
            this.size = size;
        }

        int size() {
            return size;
        }
    }

    private static final class Id {
        Id(String name, Type type) {
            this.name = name;
            this.type = type;
        }

        final String name;
        final Type type;

        @Override
        public String toString() {
            return "Id{" +
                    "name='" + name + '\'' +
                    ", type=" + type +
                    '}';
        }
    }

}
