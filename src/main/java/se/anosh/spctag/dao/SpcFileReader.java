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
		file = Paths.get(filename);
		raf = new RandomAccessFile(file.toString(), READ_ONLY);
		id666 = new Id666();

		if (!isValidSPCFile())
			throw new IOException("File is missing correct SPC-header");
		readAndSetAllFields();
		raf.close();
	}

	public String getFilename() {
		return file.toString();
	}
	
	/**
	 * 
	 * Calls all read methods and
	 * sets all the fields in the id666-object
	 * 
	 * SPC-file needs to be open for this to work.
	 * 
	 * 
	 * @throws IOException
	 */
	private void readAndSetAllFields() throws IOException {

		readHeader();
		readSongTitle();
		readGameTitle();
		
		readNameOfDumper();
		readComments();
		readDateDumpWasCreated();
		
		readHasId666Tags();
		readTagFormat();
		readArtistAndEmulatorUsedToCreateDump(); // this one depends on the tag-format being binary or text
	}

	private void readHeader() throws IOException {
		id666.setHeader(readStuff(Field.HEADER).trim()); // removes NULL character
	}

	private void readSongTitle() throws IOException {
		id666.setSongTitle(readStuff(Field.SONG_TITLE).trim());
	}
	
	private void readGameTitle() throws IOException {
		id666.setGameTitle(readStuff(Field.GAME_TITLE).trim());
	}
	
	private void readNameOfDumper() throws IOException {
		id666.setNameOfDumper(readStuff(Field.NAME_OF_DUMPER).trim());
	}
	
	private void readComments() throws IOException {
		id666.setComments(readStuff(Field.COMMENTS).trim());
	}
	
	private void readDateDumpWasCreated() throws IOException {
		id666.setDateDumpWasCreated(readStuff(Field.DUMP_DATE).trim());
	}
	
	private void readHasId666Tags() throws IOException {
		id666.setHasId666Tags(containsID666Tags());
	}
	
	private void readTagFormat() throws IOException {
		id666.setBinaryTagFormat(hasBinaryTagFormat());
	}
	
	/**
	 * This method reads the Artist & Emulator- (Used to Create Dump) fields.
	 * The offsets differ depending on the tag format being used (binary or text).
	 * 
	 * @throws IOException
	 */
	private void readArtistAndEmulatorUsedToCreateDump() throws IOException {
		
		// emulator offset to use...
		String artist = null;

		if (hasBinaryTagFormat()) {
			artist = readStuff(Field.ARTIST_OF_SONG_BINARY_FORMAT).trim();
			setEmulatorUsedToCreateDump(Field.EMULATOR_BINARY_FORMAT);
		}
		else if (id666.isTextTagFormat()) {
			artist = readStuff(Field.ARTIST_OF_SONG_TEXT_FORMAT).trim();
			setEmulatorUsedToCreateDump(Field.EMULATOR_TEXT_FORMAT);
		}
		else {
			throw new IOException("Something unthinkable occurred!");
		}
		id666.setArtist(artist); // sets it using local variable
	}
	
	private void setEmulatorUsedToCreateDump(final Field field) throws IOException {
		Objects.requireNonNull(field);
		final byte result = readByte(field); // result is the code
		EmulatorFactory factory = new ModernEmulatorFactory();
		
		// create using factory
		// Emulator's constructor has access modifier "protected"
		// use values from the Japanese spec
		Emulator emulator = factory.orderEmulator(result, Type.JAPANESE);
		id666.setEmulatorUsedToCreateDump(emulator);
	}

	private boolean isValidSPCFile() throws IOException {
		final String fileHeader = readStuff(Field.HEADER)
				.trim()
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
		}
		else {
			throw new IOException(
					String.format("%s does not contain valid value at offset: 0x%xd. Is this a SPC file?",
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

		String s = readStuff(Field.ARTIST_OF_SONG_BINARY_FORMAT);
		// If 0xB0 is *NOT* a valid char or *IS* a digit then don't allow it.
		// Sometimes we have valid digits in this offset (if the tag-format is text)
		if (!Character.isLetter(s.charAt(0)) || Character.isDigit(s.charAt(0))) {
			return false;
		} else {
			return true;
		}
	}

	private String readStuff(Field field) throws IOException {
		Objects.requireNonNull(field);
		raf.seek(field.getOffset());
		byte[] bytes = new byte[field.getLength()];
		raf.read(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	private byte readByte(Field field) throws IOException {
		assertTrue(field.getLength() == 1, "Field length must be 1 byte");
		raf.seek(field.getOffset());
		return raf.readByte();
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
		DUMP_DATE(0x9E, 11),
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




