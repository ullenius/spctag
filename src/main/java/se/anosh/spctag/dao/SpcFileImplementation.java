package se.anosh.spctag.dao;

import static se.anosh.spctag.Id666Tag.ARTIST_OF_SONG_BINARY_FORMAT_OFFSET;
import static se.anosh.spctag.Id666Tag.ARTIST_OF_SONG_LENGTH;
import static se.anosh.spctag.Id666Tag.ARTIST_OF_SONG_TEXT_FORMAT_OFFSET;
import static se.anosh.spctag.Id666Tag.COMMENTS_LENGTH;
import static se.anosh.spctag.Id666Tag.COMMENTS_OFFSET;
import static se.anosh.spctag.Id666Tag.DUMP_DATE_LENGTH;
import static se.anosh.spctag.Id666Tag.DUMP_DATE_OFFSET;
import static se.anosh.spctag.Id666Tag.EMULATOR_BINARY_FORMAT_OFFSET;
import static se.anosh.spctag.Id666Tag.EMULATOR_TEXT_FORMAT_OFFSET;
import static se.anosh.spctag.Id666Tag.GAME_TITLE_LENGTH;
import static se.anosh.spctag.Id666Tag.GAME_TITLE_OFFSET;
import static se.anosh.spctag.Id666Tag.HEADER_CONTAINS_ID666_TAG_OFFSET;
import static se.anosh.spctag.Id666Tag.HEADER_LENGTH;
import static se.anosh.spctag.Id666Tag.HEADER_OFFSET;
import static se.anosh.spctag.Id666Tag.NAME_OF_DUMPER_LENGTH;
import static se.anosh.spctag.Id666Tag.NAME_OF_DUMPER_OFFSET;
import static se.anosh.spctag.Id666Tag.SONG_TITLE_LENGTH;
import static se.anosh.spctag.Id666Tag.SONG_TITLE_OFFSET;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import se.anosh.spctag.emulator.DumpEmulator;

public class SpcFileImplementation implements SpcDao {

	private Model id666;
	private Path file;
	private RandomAccessFile raf;

	// version may vary, most recent is 0.31 (?) from 2006
	private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data"; 
	private static final int CONTAINS_ID666_TAG = 26;
	private static final int MISSING_ID666_TAG = 27;


	@Override
	public Model read() {
		Objects.requireNonNull(id666, "id666 cannot be null!");
		return this.id666;
	}

	@Override
	public void update(String song) {
		// TODO Auto-generated method stub

	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub

	}

	public SpcFileImplementation(String filename) throws FileNotFoundException, IOException {

		file = Paths.get(filename);
		raf = new RandomAccessFile(file.toString(),"r");
		id666 = new Model();

		if (!isValidSPCFile())
			throw new IOException("File is missing correct SPC-header. Exiting");
		readAll();

		raf.close();
	}

	/**
	 * Sets all the fields in the class
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	private void readAll() throws FileNotFoundException, IOException {

		id666.setHeader(readStuff(HEADER_OFFSET, HEADER_LENGTH).trim()); // removes NULL character
		id666.setSongTitle(readStuff(SONG_TITLE_OFFSET, SONG_TITLE_LENGTH).trim());
		id666.setGameTitle(readStuff(GAME_TITLE_OFFSET, GAME_TITLE_LENGTH).trim());

		id666.setNameOfDumper(readStuff(NAME_OF_DUMPER_OFFSET, NAME_OF_DUMPER_LENGTH).trim());
		id666.setComments(readStuff(COMMENTS_OFFSET, COMMENTS_LENGTH).trim());
		id666.setDateDumpWasCreated(readStuff(DUMP_DATE_OFFSET, DUMP_DATE_LENGTH).trim());

		id666.setHasId666Tags(containsID666Tags());
		id666.setBinaryTagFormat(hasBinaryTagFormat());

		// emulator offset to use...
		String artist = readStuff(ARTIST_OF_SONG_TEXT_FORMAT_OFFSET, ARTIST_OF_SONG_LENGTH).trim();

		if (hasBinaryTagFormat()) {
			artist = readStuff(ARTIST_OF_SONG_BINARY_FORMAT_OFFSET, ARTIST_OF_SONG_LENGTH).trim();
			setEmulatorUsedToCreateDump(EMULATOR_BINARY_FORMAT_OFFSET);
		}
		else if (id666.isTextTagFormat()) {
			artist = readStuff(ARTIST_OF_SONG_TEXT_FORMAT_OFFSET, ARTIST_OF_SONG_LENGTH).trim();
			setEmulatorUsedToCreateDump(EMULATOR_TEXT_FORMAT_OFFSET);
		}
		else {
			throw new IOException("Something unthinkable occured!");
		}
		id666.setArtist(artist); // sets it using local variable


		raf.close(); // close the file
	}

	private void setEmulatorUsedToCreateDump(final int offset) throws IOException {

		byte result = readByte(offset);
		id666.setEmulatorUsedToCreateDump(DumpEmulator.getName(result));
	}

	private boolean isValidSPCFile() throws IOException {
		raf.seek(0);
		final String fileHeader = readStuff(HEADER_OFFSET, HEADER_LENGTH)
				.trim()
				.substring(0, CORRECT_HEADER.length());
		return (CORRECT_HEADER.equalsIgnoreCase(fileHeader));
	}

	/**
	 * 
	 * @return
	 * @throws IOException if offset has invalid value SPC-file.
	 */
	private boolean containsID666Tags() throws IOException{

		byte tag = readByte(HEADER_CONTAINS_ID666_TAG_OFFSET);
		if (tag == CONTAINS_ID666_TAG)
			return true;
		else if (tag == MISSING_ID666_TAG)
			return false;
		else
			throw new IOException(HEADER_CONTAINS_ID666_TAG_OFFSET + " offset does not contain valid value. Is this a SPC file?");
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
	 * @return 
	 */
	private boolean hasBinaryTagFormat() throws IOException {

		String s = readStuff(ARTIST_OF_SONG_BINARY_FORMAT_OFFSET,1);
		// If 0xB0 is *NOT* a valid char or *IS* a digit then don't allow it.
		// Sometimes we have valid digits in this offset (if the tag-format is text)
		if (!Character.isLetter(s.charAt(0)) || Character.isDigit(s.charAt(0))) {
			return false;
		} else {
			return true;
		}
	}

	private String readStuff(int offset, int length) throws IOException {
		raf.seek(offset);
		byte[] bytes = new byte[length];
		raf.read(bytes);
		return new String(bytes, "ISO-8859-1");
	}

	private byte readByte(int offset) throws IOException {
		raf.seek(offset);
		byte result = raf.readByte();
		return result;
	}

	public String getFilename() {
		return file.toString();
	}

}




