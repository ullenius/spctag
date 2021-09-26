package se.anosh.spctag.dao;

import org.tinylog.Logger;
import se.anosh.spctag.domain.Xid6;
import se.anosh.spctag.domain.Xid6Tag;

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

import static java.lang.Integer.toHexString;

final class Xid6Reader {

    private static final Map<Byte, Id> mappningar = new HashMap<>();
    private static final long XID6_OFFSET = 0x10200L;
    static {
        mappningar.put((byte) 0x1, new Id(Xid6Tag.SONG, Type.TEXT));
        mappningar.put((byte) 0x2, new Id(Xid6Tag.GAME, Type.TEXT));
        mappningar.put((byte) 0x3, new Id(Xid6Tag.ARTIST, Type.TEXT));
        mappningar.put((byte) 0x4, new Id(Xid6Tag.DUMPER, Type.TEXT));
        mappningar.put((byte) 0x6, new Id(Xid6Tag.EMULATOR, Type.DATA));
        mappningar.put((byte) 0x7, new Id(Xid6Tag.COMMENTS, Type.TEXT));
        mappningar.put((byte) 0x10, new Id(Xid6Tag.OST_TITLE, Type.TEXT));
        mappningar.put((byte) 0x11, new Id(Xid6Tag.OST_DISC, Type.DATA));
        mappningar.put((byte) 0x12, new Id(Xid6Tag.OST_TRACK, Type.OST));
        mappningar.put((byte) 0x13, new Id(Xid6Tag.PUBLISHER, Type.TEXT));
        mappningar.put((byte) 0x14, new Id(Xid6Tag.COPYRIGHT_YEAR, Type.YEAR));
        // song info stuff
        mappningar.put((byte) 0x30, new Id(Xid6Tag.INTRO, Type.INTRO));
        mappningar.put((byte) 0x31, new Id(Xid6Tag.LOOP_LENGTH, Type.NUMBER));
        mappningar.put((byte) 0x32, new Id(Xid6Tag.END, Type.NUMBER));
        mappningar.put((byte) 0x33, new Id(Xid6Tag.FADE, Type.NUMBER));
        mappningar.put((byte) 0x34, new Id(Xid6Tag.MUTED, Type.MUTED));
        mappningar.put((byte) 0x35, new Id(Xid6Tag.LOOP_TIMES, Type.DATA));
        mappningar.put((byte) 0x36, new Id(Xid6Tag.MIXING, Type.NUMBER));
    }

    private final Path filename;
    private Xid6 xid6 = null;

    final BiConsumer<Id, byte[]> year = (id, data) -> xid6.setYear(toShort(data));
    final BiConsumer<Id, byte[]> muted = (id, data) -> xid6.setMutedChannels( (short) (data[0] & 0xFF) );

    final BiConsumer<Id, byte[]> ost = (id, data) -> {
        byte hibyte = data[0];
        byte lobyte = data[1];
        boolean hasHiByte = hibyte != (byte) 0;
        Logger.debug("Has hi byte: {}", hasHiByte);

        if (lobyte < 0 || lobyte > 99) {
            throw new IllegalStateException("track no is invalid: " + lobyte);
        }
        Xid6.OstTrack ostTrack = (hasHiByte && isAscii(hibyte))
                ? new Xid6.OstTrack(lobyte, (char) hibyte)
                : new Xid6.OstTrack(lobyte);
        xid6.setOstTrack(ostTrack);
    };

    private static boolean isAscii(int ch) {
        return ch >= (int) 'a' && ch <= (int) 'z'
                || ch >= (int) 'A' && ch <= (int) 'Z';
    }

    final BiConsumer<Id, byte[]> oneByteData = (id, data) -> setData(id.tag, data[0]);

    private void setData(Xid6Tag tag, byte b) {
        switch (tag) {
            case EMULATOR:
                xid6.setEmulator(b);
                break;
            case OST_DISC:
                xid6.setOstDisc(b);
                break;
            case LOOP_TIMES:
                xid6.setLoop(b);
                break;
            default:
                throw new IllegalArgumentException("no mapping found for: " + tag);
        }
    }

    private final Map<Type, BiConsumer<Id, byte[]>> mappedBehaviourDataStoredInHeader = Map.of(
            Type.OST, ost,
            Type.YEAR, year,
            Type.MUTED, muted,
            Type.DATA, oneByteData
    );

    Xid6Reader(String filename) throws IOException {
        this.filename = Paths.get(filename);
        parseXid6(this.filename);
    }

    private void parseXid6(Path spc) throws IOException {
        final long fileSize = Files.size(spc);
        final long xid6Size = fileSize - XID6_OFFSET;
        final long xid6SizeMinusHeader = xid6Size - 8; // size of header
        if (fileSize <= XID6_OFFSET) {
            throw new IOException("File too small. Does not contain xid6");
        }
        xid6 = new Xid6();

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

            if (dataStoredInHeader) {
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
                        setText(mappatId.tag, text);
                        break;
                    case INTRO:
                        if (type.size() == Integer.BYTES) {
                            xid6.setIntro(subChunks.getInt());
                        }
                        break;
                    case NUMBER:
                        if (type.size() == Integer.BYTES) {
                            setNumber(mappatId.tag, subChunks.getInt());
                        }
                        break;
                    default:
                        throw new IllegalStateException("no mapping for type: " + type);
                }
            }
        }
        fileChannel.close();
    }

    private void setNumber(Xid6Tag tag, int num) {
        switch (tag) {
            case DATE:
                xid6.setDate(num);
                break;
            case LOOP_LENGTH:
                xid6.setLoop((byte) num);
                break;
            case END:
                xid6.setEndLength(num);
                break;
            case FADE:
                xid6.setFadeLength(num);
                break;
            case MUTED:
                xid6.setMutedChannels((short) num);
                break;
            case MIXING:
                xid6.setMixingLevel( (byte) num);
                break;
            default:
                throw new IllegalArgumentException("no mapping found for: " + tag);
        }
    }

    private void setText(Xid6Tag tag, String text) {
        switch (tag) {
            case SONG:
                xid6.setSong(text);
                break;
            case GAME:
                xid6.setGame(text);
                break;
            case ARTIST:
                xid6.setArtist(text);
                break;
            case DUMPER:
                xid6.setDumper(text);
                break;
            case COMMENTS:
                xid6.setComments(text);
                break;
            case OST_TITLE:
                xid6.setOstTitle(text);
                break;
            case PUBLISHER:
                xid6.setPublisher(text);
                break;
            default:
                throw new IllegalArgumentException("no mapping found for: " + tag);
        }
    }

    private short toShort(byte buf[]) { // little endian
        return (short) (
                (((buf[1] & 0xFF) << 8) | (buf[0]) & 0xFF));
    }

    Xid6 getXid6() {
        Objects.requireNonNull(xid6, "xid6 cannot be null!");
        return this.xid6;
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
