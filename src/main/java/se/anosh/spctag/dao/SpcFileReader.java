package se.anosh.spctag.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import se.anosh.spctag.emulator.factory.Emulator;
import se.anosh.spctag.emulator.factory.EmulatorFactory;
import se.anosh.spctag.emulator.factory.EmulatorFactory.Type;
import se.anosh.spctag.emulator.factory.ModernEmulatorFactory;

public class SpcFileReader {
	
	//ID666 tag offsets used in SPC-files (Sony SPC-700 sound chip)
    public static final int HEADER_CONTAINS_ID666_TAG_OFFSET = 0x23; // 1 byte
    public static final int HEADER_OFFSET = 0;
    public static final int HEADER_LENGTH = 33;

    public static final int SONG_TITLE_OFFSET = 0x2E;
    public static final int SONG_TITLE_LENGTH = 32;
    
    public static final int GAME_TITLE_OFFSET = 0x4E;
    public static final int GAME_TITLE_LENGTH = 32;
    
    public static final int NAME_OF_DUMPER_OFFSET = 0x6E;
    public static final int NAME_OF_DUMPER_LENGTH = 16;
    
    public static final int COMMENTS_OFFSET = 0x7E;
    public static final int COMMENTS_LENGTH = 32;
    
    public static final int DUMP_DATE_OFFSET = 0x9E;
    public static final int DUMP_DATE_LENGTH = 11;
    
    public static final int ARTIST_OF_SONG_TEXT_FORMAT_OFFSET = 0xB1; // text format
    public static final int ARTIST_OF_SONG_BINARY_FORMAT_OFFSET = 0xB0;
    public static final int ARTIST_OF_SONG_LENGTH = 32;
    
    public static final int EMULATOR_TEXT_FORMAT_OFFSET = 0xD2;
    public static final int EMULATOR_BINARY_FORMAT_OFFSET = 0xD1;
    public static final int EMULATOR_LENGTH = 1;
	

	private Id666 id666;
	private Path file;
	private RandomAccessFile raf;

	// version may vary, most recent is 0.31 (?) from 2006
	private static final String CORRECT_HEADER = "SNES-SPC700 Sound File Data"; 
	private static final byte CONTAINS_ID666_TAG = 26;
	private static final byte MISSING_ID666_TAG = 27;


	public Id666 getId666() {
		Objects.requireNonNull(id666, "id666 cannot be null!");
		return this.id666;
	}

	// Constructor
	public SpcFileReader(String filename) throws FileNotFoundException, IOException {

		file = Paths.get(filename);
		raf = new RandomAccessFile(file.toString(),"r");
		id666 = new Id666();

		if (!isValidSPCFile())
			throw new IOException("File is missing correct SPC-header. Exiting");
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
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	private void readAndSetAllFields() throws FileNotFoundException, IOException {

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
		id666.setHeader(readStuff(HEADER_OFFSET, HEADER_LENGTH).trim()); // removes NULL character
	}

	private void readSongTitle() throws IOException {
		id666.setSongTitle(readStuff(SONG_TITLE_OFFSET, SONG_TITLE_LENGTH).trim());
	}
	
	private void readGameTitle() throws IOException {
		id666.setGameTitle(readStuff(GAME_TITLE_OFFSET, GAME_TITLE_LENGTH).trim());
	}
	
	private void readNameOfDumper() throws IOException {
		id666.setNameOfDumper(readStuff(NAME_OF_DUMPER_OFFSET, NAME_OF_DUMPER_LENGTH).trim());
	}
	
	private void readComments() throws IOException {
		id666.setComments(readStuff(COMMENTS_OFFSET, COMMENTS_LENGTH).trim());
	}
	
	private void readDateDumpWasCreated() throws IOException {
		id666.setDateDumpWasCreated(readStuff(DUMP_DATE_OFFSET, DUMP_DATE_LENGTH).trim());
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
	}
	
	
	
	private void setEmulatorUsedToCreateDump(final int offset) throws IOException {

		byte result = readByte(offset); // result is the code
		
		// creating factory
		EmulatorFactory factory = new ModernEmulatorFactory();
		
		// create using factory
		// Emulator's constructor has access modifier "protected"
		// use values from the Japanese spec
		Emulator emulator = factory.orderEmulator(result, Type.JAPANESE);
		id666.setEmulatorUsedToCreateDump(emulator);
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



}




