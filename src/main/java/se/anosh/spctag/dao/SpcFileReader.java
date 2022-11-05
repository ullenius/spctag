package se.anosh.spctag.dao;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Objects;

import se.anosh.spctag.domain.Id666;
import se.anosh.spctag.emulator.factory.*;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;

final class SpcFileReader {

	private final Id666 id666;
	private final Path file;
	private final RandomAccessFile raf;

	// version may vary, most recent is 0.31 (?) from 2006
	private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data";
	private static final byte CONTAINS_ID666_TAG = 26;
	private static final byte MISSING_ID666_TAG = 27;

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
	 * sets all the fields in the id666-object
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
		readTagFormat();

		// these depend on the tag-format being binary or text
		readArtist();
		readEmulatorUsedToCreateDump();
		readDateDumpWasCreated();
	}

	private void readHeader() throws IOException {
		id666.setHeader(parse(Field.HEADER)); // removes NULL character
	}

	private void readSongTitle() throws IOException {
		id666.setSongTitle(parse(Field.SONG_TITLE));
	}
	
	private void readGameTitle() throws IOException {
		id666.setGameTitle(parse(Field.GAME_TITLE));
	}
	
	private void readNameOfDumper() throws IOException {
		id666.setNameOfDumper(parse(Field.NAME_OF_DUMPER));
	}
	
	private void readComments() throws IOException {
		id666.setComments(parse(Field.COMMENTS));
	}
	
	private void readDateDumpWasCreated() throws IOException {
		id666.setDateDumpWasCreated(parse(Field.DUMP_DATE_TEXT_FORMAT));
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
				? parse(Field.ARTIST_OF_SONG_BINARY_FORMAT)
				: parse(Field.ARTIST_OF_SONG_TEXT_FORMAT);
	}

	private void readEmulatorUsedToCreateDump() throws IOException {
		setEmulatorUsedToCreateDump(detectOffsetEmulatorUsedToCreateDump());
	}

	private Field detectOffsetEmulatorUsedToCreateDump() { // emulator offset to use...
		return id666.isBinaryTagFormat()
				? Field.EMULATOR_BINARY_FORMAT
				: Field.EMULATOR_TEXT_FORMAT;
	}
	
	private void setEmulatorUsedToCreateDump(final Field field) throws IOException {
		Objects.requireNonNull(field);
		final byte emulatorCode = readByte(field);
		EmulatorFactory factory = new ModernEmulatorFactory();
		
		// use values from the Japanese spec
		Emulator emulator = factory.orderEmulator(emulatorCode, Type.JAPANESE);
		id666.setEmulatorUsedToCreateDump(emulator);
	}

	private boolean isValidSPCFile() throws IOException {
		final String fileHeader = parse(Field.HEADER)
				.substring(0, CORRECT_HEADER.length());
		return CORRECT_HEADER.equalsIgnoreCase(fileHeader);
	}

	/**
	 * 
	 * @return
	 * @throws IOException if offset has invalid value SPC-file.
	 */
	private boolean containsID666Tags() throws IOException{
		final byte tag = readByte(Field.HEADER_CONTAINS_ID666_TAG);
		if (tag == CONTAINS_ID666_TAG) {
			return true;
		}
		else if (tag == MISSING_ID666_TAG) {
			return false;
		} else {
			throw new IOException(String.format("%s does not contain valid value at offset: 0x%xd. Is this a SPC file?",
					Field.HEADER_CONTAINS_ID666_TAG, Field.HEADER_CONTAINS_ID666_TAG.offset));
		}
	}

	/**
	 * 
	 * Checks if tag format is text format (as opposed to binary)
	 * It is kind of ambigious which format is used since there are
	 * no real inidcator in the file format specification.
	 * 
	 * BUGS:
	 * This method only works if the artist field is set...
	 * and if the artist name doesn't start with a digit
	 * 
	 * On the other hand... The only other value that is affected
	 * is the single byte that determines the emulator used for creating
	 * the dump. And who cares? It's not even properly set in most SPC-files.
	 * 
	 * @return true if spc has binary tag format
	 */
	private boolean hasBinaryTagFormat() throws IOException {
		final char first = parse(Field.ARTIST_OF_SONG_BINARY_FORMAT).charAt(0);
		// If 0xB0 is *NOT* a valid char or *IS* a digit then don't allow it.
		// Sometimes we have valid digits in this offset (if the tag-format is text)
		return Character.isLetter(first) && !Character.isDigit(first);
	}

	private String parse(Field field) throws IOException {
		Objects.requireNonNull(field);
		raf.seek(field.getOffset());
		byte[] bytes = new byte[field.getLength()];
		raf.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8).trim(); // remove NULL characters
	}

	private byte readByte(Field field) throws IOException {
		assertTrue(field.getLength() == 1, "Field length must be 1 byte");
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

	private enum Field {
		HEADER(0x00, 33),
		SONG_TITLE(0x2E, 32),
		GAME_TITLE(0x4E, 32),
		NAME_OF_DUMPER(0x6E, 16),
		COMMENTS(0x7E, 32),
		DUMP_DATE_TEXT_FORMAT(0x9E, 11), // FIXME implement binary format
		DUMP_DATE_BINARY_FORMAT(0x9E, 4),
		ARTIST_OF_SONG_TEXT_FORMAT(0xB1, 32),
		ARTIST_OF_SONG_BINARY_FORMAT(0xB0, 32),
		EMULATOR_TEXT_FORMAT(0xD2, 1),
		EMULATOR_BINARY_FORMAT(0xD1, 1),

		HEADER_CONTAINS_ID666_TAG(0x23, 1);

		private final int length;
		private final int offset;

		Field(int offset, int length) {
			this.offset = offset;
			this.length = length;
		}

		int getLength() {
			return length;
		}

		int getOffset() {
			return offset;
		}
	}

}
