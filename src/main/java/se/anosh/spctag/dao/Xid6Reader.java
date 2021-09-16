package se.anosh.spctag.dao;

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
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.toHexString;

public class Xid6Reader {

    private static final Map<Byte, Id> mappningar = new HashMap<>();

    static {
        mappningar.put((byte) 0x1, new Id("Song name", Type.TEXT));
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
        mappningar.put((byte) 0x34, new Id("Mutes voices", Type.MUTED)); // print bits
        mappningar.put((byte) 0x35, new Id("Number of times to loop", Type.DATA));
        mappningar.put((byte) 0x36, new Id("Mixing (preamp) level", Type.NUMBER));
    }

    final Consumer<byte[]> year = (data) -> System.out.println("Year: " + toShort(data));
    final Consumer<byte[]> muted = (data) -> {
        System.out.print("Muted channel (bit set for each muted channel): ");
        final byte muted = data[0];
        for (int i = 0; i < 8; i++) {
            System.out.print(((1 << i) & muted) != 0 ? 0 : 1);
        }
        System.out.println();
    };
    final Consumer<byte[]> ost = (data) -> {
        byte hibyte = data[0];
        byte lobyte = data[1];
        boolean hasHiByte = hibyte != (byte) 0;
        System.out.println("Has hi byte: " + hasHiByte);

        System.out.println("Upper byte (char): " + ((hasHiByte)
                ? Character.getNumericValue(Byte.toUnsignedInt(hibyte))
                : "0"));
        System.out.println("Lower byte (number): " + lobyte);
        if (lobyte < 0 || lobyte > 99) {
            throw new IllegalStateException("track no is invalid: " + lobyte);
        }
    };
    final Consumer<byte[]> oneByteData = (data) -> System.out.println("Data (1 byte): " + data[0]);

    private final Map<Type, Consumer<byte[]>> mappedBehaviourDataStoredInHeader = Map.of(
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
        List<Path> files = listFilesUsingFilesList("");
        System.out.println("Size of set: " + files.size());
        List<Byte> unknownMappings = new LinkedList<>();
        List<String> unknownMappingfiles = new LinkedList<>();
        List<String> filesWith20Tag = new LinkedList<>();

        final long offset = 0x10200;

        //Path spc = Paths.get("/tmp/foobar.spc");
        for (Path spc : files) {
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
            System.out.println("Position: " + buffer.position());
            System.out.println("Limit: " + buffer.limit());

            byte[] magic = new byte[4];
            buffer.get(magic);
            System.out.println(new String(magic, StandardCharsets.UTF_8));

            // workaround if header is broken and actual filesize is less than chunk size in header
            int tmp = buffer.getInt();
            final int chunkSize = (tmp > xid6SizeMinusHeader) ? (int) xid6SizeMinusHeader : tmp;

            System.out.println("Chunk size: " + chunkSize);
            System.out.println("Current pos: " + buffer.position());

            byte[] subChunkArr = new byte[chunkSize]; // chunkSize exclusive header
            buffer.get(subChunkArr); // contains all sub-chunks, including sub-chunk headers
            var subChunks = ByteBuffer.wrap(subChunkArr).order(ByteOrder.LITTLE_ENDIAN); // FIXME felaktigt namn, är hela subchunken

            System.out.println("Subchunk contents");
            while (subChunks.position() < subChunks.limit()) {
                System.out.println("-----------");
                System.out.println("Subchunk header");
                System.out.println("Current pos: " + subChunks.position());

                byte id = subChunks.get();
                if (!mappningar.containsKey(id)) {
                    unknownMappings.add(id);
                    unknownMappingfiles.add(spc.getFileName().toString());
                    if (id == (byte)0x20) {
                        filesWith20Tag.add(spc.getFileName() + " har 0x20-taggen");
                    }
                    break;
                }
                Id mappatId = mappningar.get(id);
                Type type = mappatId.type;

                boolean dataStoredInHeader = subChunks.get() == 0 || type == Type.DATA; // workaround for broken type byte, always read from header

                System.out.println("Id: 0x" + toHexString(id));
                System.out.println("Mappat id: " + mappatId);
                System.out.println("Data stored in header: " + dataStoredInHeader);

                int size = (dataStoredInHeader)
                        ? mappatId.type.size()
                        : subChunks.getShort(); // changes current offset
                System.out.println("Size: " + size);

                if (dataStoredInHeader) { // max 2 bytes
                    if (size > 2) {
                        throw new IllegalStateException("Data stored in header. Yet size is larger than 2 bytes");
                    }
                    byte[] data = new byte[2]; // always allocate 2 bytes
                    subChunks.get(data);

                    var func = mappedBehaviourDataStoredInHeader.get(type);
                    func.accept(data); // print info

                } else {
                    switch (type) {
                        case TEXT:
                            // peek at last byte, workaround for broken tags
                            if ((size-1) % 4 == 0) {
                                final int peekPos = size - 1;
                                final int oldPos = subChunks.position();
                                byte peek = subChunks.get(oldPos + peekPos);
                                System.out.println("Peek byte: " + peek);
                                if (mappningar.containsKey(peek)) {
                                    size--;
                                }
                            }

                            int bufsize = size;
                            while (bufsize % 4 != 0) {
                                bufsize++;
                                System.out.println("Increasing bufsize: " + bufsize);
                            }

                            byte buf[] = new byte[bufsize];
                            subChunks.get(buf);
                            String text = new String(buf, StandardCharsets.UTF_8).trim();
                            System.out.println("Text: " + text);
                            break;
                        case INTRO:
                            if (type.size == Integer.BYTES) {
                                int num = subChunks.getInt();
                                System.out.println("Number: " + num);
                                System.out.println("In seconds: " + num / 640000.0);
                            }
                            break;
                        case NUMBER:
                            if (type.size == Integer.BYTES) {
                                int num = subChunks.getInt();
                                System.out.println("Number: " + num);
                            }
                            break;
                        default:
                            throw new IllegalStateException("no mapping for type: " + type);
                    }
                }
            }
            fileChannel.close();
        }
        System.out.println("Unknown mappings: " + unknownMappings);
        System.out.println("Files with 0x20: " + filesWith20Tag);
        Collections.sort(unknownMappingfiles);
        System.out.println("Files with unkown mappings " + unknownMappingfiles);
    }

    private static short toShort(byte buf[]) { // little endian
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
