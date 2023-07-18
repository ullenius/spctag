package se.anosh.spctag.dao;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.function.Function;

import org.tinylog.Logger;
import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.emulator.factory.*;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

final class SpcFileReader {

	private final Id666 id666;
	private final Path file;
	private final RandomAccessFile raf;

	// version may vary, most recent is 0.31 (?) from 2006
	private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data";
	private static final byte CONTAINS_ID666_TAG = 0x1A;
	private static final byte MISSING_ID666_TAG = 0x1B;
	private static final String READ_ONLY = "r";

	public Id666 getId666() {
		Objects.requireNonNull(id666, "id666 cannot be null!");
		return this.id666;
	}

	public SpcFileReader(final String filename) throws IOException {
		file = Path.of(filename);
		raf = new RandomAccessFile(file.toString(), READ_ONLY);
		id666 = new Id666();
		if (!isValidSPCFile())
			throw new IOException("File is missing correct SPC-header");
		readAndSetAllFields();
		raf.close();
	}

	/**
	 *
	 * Calls all read methods and
	 * sets all the Id666.Fields in the id666-object
	 * SPC-file needs to be open for this to work.
	 *
	 */
	private void readAndSetAllFields() throws IOException {
		readHeader();
		readSongTitle();
		readGameTitle();

		readNameOfDumper();
		readComments();

		readHasId666Tags();
		readVersion();
		readTagFormat();

		// these depend on the tag-format being binary or text
		readLengthSeconds();
		readFadeLength();
		readArtist();
		readEmulatorUsedToCreateDump();
		readDateDumpWasCreated();
	}

	private void readHeader() throws IOException {
		id666.setHeader(parse(Id666.Field.HEADER));
	}

	private void readSongTitle() throws IOException {
		id666.setSongTitle(parse(Id666.Field.SONG_TITLE));
	}

	private void readGameTitle() throws IOException {
		id666.setGameTitle(parse(Id666.Field.GAME_TITLE));
	}

	private void readNameOfDumper() throws IOException {
		id666.setNameOfDumper(parse(Id666.Field.NAME_OF_DUMPER));
	}

	private void readComments() throws IOException {
		id666.setComments(parse(Id666.Field.COMMENTS));
	}

	private void readDateDumpWasCreated() throws IOException {
		if (id666.isBinaryTagFormat() && hasBinaryDumpDate()) {
			id666.setDateDumpWasCreated(parseBinaryDumpDate());
		}
		else if (id666.isTextTagFormat()) {
			id666.setDateDumpWasCreated(parse(Id666.Field.DUMP_DATE_TEXT_FORMAT));
		}
	}

	private boolean hasBinaryDumpDate() throws IOException {
		return readByte(Id666.Field.DUMP_DATE_BINARY_FORMAT) != 0;
	}

	private LocalDate parseBinaryDumpDate() throws IOException {
		return parse(Id666.Field.DUMP_DATE_BINARY_FORMAT, (bytes) -> {
			var buffer = ByteBuffer.wrap(bytes)
					.order(ByteOrder.LITTLE_ENDIAN);
			final byte day = buffer.get();
			final byte month = buffer.get();
			final short year = buffer.getShort();
			return LocalDate.of(year, month, day);
		});
	}

	private void readHasId666Tags() throws IOException {
		id666.setHasId666Tags(containsID666Tags());
	}

	private void readTagFormat() throws IOException {
		id666.setBinaryTagFormat(hasBinaryTagFormat());
	}

	private void readArtist() throws IOException {
		id666.setArtist(parseArtist());
	}

	private String parseArtist() throws IOException {
		return id666.isBinaryTagFormat()
				? parse(Id666.Field.ARTIST_OF_SONG_BINARY_FORMAT)
				: parse(Id666.Field.ARTIST_OF_SONG_TEXT_FORMAT);
	}

	private void readLengthSeconds() throws IOException {
		id666.setLengthSeconds(parseLengthSeconds());
	}

	private int parseLengthSeconds() throws IOException {
		if (id666.isBinaryTagFormat()) {
			return parse(Id666.Field.LENGTH_SECONDS, (bytes) -> {
				// 24-bit unsigned number (little endian)
				return (bytes[0] & 0xFF) | (bytes[1] << 8 & 0xFF00) | (bytes[2] << 16 & 0xFF0000);
			});
		}
		String length = parse(Id666.Field.LENGTH_SECONDS);
		return length.isBlank() ? 0 : Integer.parseInt(length);
	}

	private void readFadeLength() throws IOException {
		id666.setFadeLengthMilliseconds(parseFadeLengthMilliseconds());
	}

	private long parseFadeLengthMilliseconds() throws IOException {
		if (id666.isBinaryTagFormat()) {
			return parse(Id666.Field.FADE_LENGTH_MILLISECONDS_BINARY_FORMAT, (bytes) -> {
				// 32-bit unsigned number (little endian)
				long val = (bytes[0] & 0xFF) | (bytes[1] << 8 & 0xFF00) | (bytes[2] << 16 & 0xFF0000)
						| (bytes[3] << 24 & 0xFF000000L);
				return val;
			});
		}
		final String fadelength = parse(Id666.Field.FADE_LENGTH_MILLISECONDS_TEXT_FORMAT);
		return fadelength.isBlank() ? 0 : Long.parseLong(fadelength);
	}

	private void readVersion() throws IOException {
		short headerVersion = Short.parseShort(id666.getHeader().substring(CORRECT_HEADER.length() + 1 + 3)); // off-by-one plus "v0."-prefix
		short byteVersion = parseVersion();
		if (headerVersion != parseVersion()) {
			Logger.warn("Minor version in header does not match binary tag: {} and {}", headerVersion, byteVersion);
		}
		id666.setVersion(byteVersion);
	}

	private short parseVersion() throws IOException {
		short minorVersion = readByte(Id666.Field.VERSION_MINOR);
		return (short) (minorVersion & 0xFF);
	}


	private void readEmulatorUsedToCreateDump() throws IOException {
		setEmulatorUsedToCreateDump(detectOffsetEmulatorUsedToCreateDump());
	}

	private Id666.Field detectOffsetEmulatorUsedToCreateDump() {
		return id666.isBinaryTagFormat()
				? Id666.Field.EMULATOR_BINARY_FORMAT
				: Id666.Field.EMULATOR_TEXT_FORMAT;
	}

	private void setEmulatorUsedToCreateDump(final Id666.Field field) throws IOException {
		Objects.requireNonNull(field);
		final short emulatorCode = readByte(field);
		// use values from the Japanese spec
		Emulator emulator = EmulatorFactory.createEmulator(emulatorCode, Type.JAPANESE);
		id666.setEmulatorUsedToCreateDump(emulator);
	}

	private boolean isValidSPCFile() throws IOException {
		final String fileHeader = parse(Id666.Field.HEADER)
				.substring(0, CORRECT_HEADER.length());
		return CORRECT_HEADER.equalsIgnoreCase(fileHeader);
	}

	/**
	 *
	 * @throws IOException if offset has invalid value SPC-file.
	 */
	private boolean containsID666Tags() throws IOException{
		final short tag = readByte(Id666.Field.HEADER_CONTAINS_ID666_TAG);
		if (tag == CONTAINS_ID666_TAG) {
			return true;
		}
		else if (tag == MISSING_ID666_TAG) {
			return false;
		} else {
			throw new IOException(String.format("%s does not contain valid value at offset: 0x%xd. Is this a SPC file?",
					Id666.Field.HEADER_CONTAINS_ID666_TAG, Id666.Field.HEADER_CONTAINS_ID666_TAG.getOffset()));
		}
	}

	/**
	 *
	 * Checks if tag format is text format (as opposed to binary)
	 * It is kind of ambigious which format is used since there are
	 * no real indicators in the file format specification.
	 * --
	 * BUGS:
	 * This method only works if the artist field is set...
	 * and if the artist name doesn't start with a digit
	 */
	private boolean hasBinaryTagFormat() throws IOException {
		final char first = parse(Id666.Field.ARTIST_OF_SONG_BINARY_FORMAT,
				(bytes) -> new String(bytes, StandardCharsets.UTF_8)) // don't cleanup result
				.charAt(0);
		// If 0xB0 is *NOT* a valid char or *IS* a digit then don't allow it.
		// Sometimes we have valid digits in this offset (if the tag-format is text)
		return Character.isLetter(first) && !Character.isDigit(first);
	}

	private String parse(Id666.Field field) throws IOException {
		Function<byte[], String> func = (bytes) -> new String(bytes, StandardCharsets.UTF_8).trim(); // remove NULL characters;
		return parse(field, func);
	}

	private <R> R parse(Id666.Field field, Function<byte[], R> func) throws IOException {
		Objects.requireNonNull(field);
		raf.seek(field.getOffset());
		byte[] bytes = new byte[field.getLength()];
		raf.read(bytes);
		return func.apply(bytes);
	}

	private short readByte(Id666.Field field) throws IOException {
		assertTrue(field == Id666.Field.DUMP_DATE_BINARY_FORMAT || field.getLength() == 1, "Field length must be 1 byte");
		raf.seek(field.getOffset());
		return raf.readByte();
	}

	public String getFilename() {
		return file.toString();
	}

	private static void assertTrue(boolean expression, final String message) {
		if (!expression) {
			throw new IllegalArgumentException(message);
		}
	}

}
